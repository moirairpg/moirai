package es.thalesalv.chatrpg.application.helper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Bump;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.Nudge;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class MessageFormatHelper {

    private static final String SAID = " said: ";
    private static final String ROLE_USER = "user";
    private static final String ROLE_SYSTEM = "system";
    private static final String TAG_EXPRESSION = "(@|)";
    private static final String BOT_SAID = "{0}\n{1} said: ";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String REMEMBER_TO_NEVER = "I will remember to never";
    private static final String CHARACTER_DESCRIPTION = "{0} description: {1}";
    private static final String CHAT_EXPRESSION = "^(.*) (says|said|quoted|replied).*";
    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFormatHelper.class);

    /**
     * Extracts player characters entries from database given the player's Discord user ID
     * @param entriesFound List of entries found in the messages until now
     * @param messages List of messages in the channel
     * @param player Player user
     * @param mentions Mentioned users (their characters are extracted too)
     * @param world World containing the lore entries that should be used for matching
     */
    public void handlePlayerCharacterEntries(final Set<LorebookEntry> entriesFound, final List<String> messages,
            final User player, final Mentions mentions, final World world) {

        LOGGER.debug("Entered player character entry handling");
        final Set<LorebookEntry> entries =  world.getLorebook().getEntries();
        entries.stream()
                .filter(entry -> entry.getPlayerDiscordId().equals(player.getId()))
                .findFirst()
                .ifPresent(entry -> {
                    entriesFound.add(entry);
                    messages.replaceAll(m -> m.replaceAll(player.getAsTag(), entry.getName())
                            .replaceAll(TAG_EXPRESSION + player.getName(), entry.getName()));
                });

        mentions.getUsers().forEach(mention -> entries.stream()
                .filter(entry -> entry.getPlayerDiscordId().equals(mention.getId()))
                .findFirst()
                .ifPresent(entry -> {
                    entriesFound.add(entry);
                    messages.replaceAll(m -> m.replaceAll(mention.getAsTag(), entry.getName())
                            .replaceAll(TAG_EXPRESSION + mention.getName(), entry.getName()));
                }));
    }

    /**
     * Extracts lore entries from the conversation when they're mentioned by name
     * @param messageList List of messages in the channel
     * @param world World containing the lore entries that should be used for matching
     * @return Set containing all entries found
     */
    public Set<LorebookEntry> handleEntriesMentioned(final List<String> messageList, final World world) {

        LOGGER.debug("Entered mentioned entries handling");
        final String messages = String.join("\n", messageList);
        return world.getLorebook().getEntries().stream()
                .map(entry -> {
                    Pattern p = Pattern.compile(entry.getRegex());
                    Matcher matcher = p.matcher(messages);
                    if (matcher.find()) {
                        return entry;
                    }

                    return null;
                })
                .filter(r -> null != r)
                .collect(Collectors.toSet());
    }

    public void processEntriesFoundForRpg(final Set<LorebookEntry> entriesFound, final List<String> messages, final JDA jda) {

        entriesFound.forEach(entry -> {
            if (StringUtils.isNotBlank(entry.getPlayerDiscordId())) {
                messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, entry.getName()));
            }

            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription()));
            Optional.ofNullable(entry.getPlayerDiscordId()).ifPresent(id -> {
                final User p = jda.retrieveUserById(id).complete();
                messages.replaceAll(m -> m.replaceAll(p.getAsTag(), entry.getName())
                        .replaceAll(TAG_EXPRESSION + p.getName(), entry.getName()));
            });
        });
    }

    public void processEntriesFoundForChat(final Set<LorebookEntry> entriesFound, final List<String> messages) {

        entriesFound.forEach(entry ->
            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription())));
    }

    public List<ChatMessage> formatMessagesForChatCompletions(final List<String> messages, final EventData eventData, final StringProcessor inputProcessor) {

        final Persona persona = eventData.getChannelDefinitions().getChannelConfig().getPersona();
        final String personality = inputProcessor.process(persona.getPersonality());
        List<ChatMessage> chatMessages = messages.stream()
                .filter(msg -> !msg.trim().equals((persona.getName() + SAID).trim()))
                .map(msg -> inputProcessor.process(msg))
                .map(msg -> ChatMessage.builder()
                            .role(determineRole(msg, persona))
                            .content(formatBotName(msg, persona))
                            .build())
                .collect(Collectors.toList());

        chatMessages.add(0, ChatMessage.builder()
                .role(ROLE_SYSTEM)
                .content(personality)
                .build());

        chatMessages = formatNudgeForChatCompletions(persona, chatMessages, inputProcessor);
        chatMessages = formatBumpForChatCompletions(persona, chatMessages, inputProcessor);
        return chatMessages;
    }

    private String formatBotName(final String msg, final Persona persona) {

        return msg.replaceAll(persona.getName() + SAID, StringUtils.EMPTY);
    }

    private String determineRole(final String message, final Persona persona) {

        final boolean isChat = message.matches(CHAT_EXPRESSION);
        if (message.startsWith(persona.getName())) {
            return ROLE_ASSISTANT;
        } else if (isChat && !message.startsWith(REMEMBER_TO_NEVER)) {
            return ROLE_USER;
        }

        return ROLE_SYSTEM;
    }

    /**
     * Stringifies messages and turns them into a prompt format
     *
     * @param messages Messages in the chat room
     * @param eventData Object containing event data
     * @return Stringified messages for prompt
     */
    public String chatifyMessages(final List<String> messages, final EventData eventData, final StringProcessor inputProcessor) {

        LOGGER.debug("Entered chatbot conversation formatter");
        final Persona persona = eventData.getChannelDefinitions().getChannelConfig().getPersona();
        messages.replaceAll(m -> m.replace(eventData.getBot().getName(), persona.getName()).trim());
        final String promptContent = MessageFormat.format(BOT_SAID,
                String.join(StringUtils.LF, messages), persona.getName()).trim();

        return inputProcessor.process(promptContent);
    }

    public List<ChatMessage> formatNudgeForChatCompletions(final Persona persona, final List<ChatMessage> messages, final StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .map(ndge -> {
                    messages.add(messages.stream()
                        .filter(m -> "user".equals(m.getRole()))
                        .mapToInt(messages::indexOf)
                        .reduce((a, b) -> b)
                        .orElse(0) + 1,
                        ChatMessage.builder()
                                .role(ndge.role)
                                .content(inputProcessor.process(ndge.content))
                                .build());

                    return messages;
                })
                .orElse(messages);
    }

    public List<ChatMessage> formatBumpForChatCompletions(final Persona persona, final List<ChatMessage> messages, final StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .map(bmp -> {
                    ChatMessage bumpMessage = ChatMessage.builder()
                            .role(bmp.role)
                            .content(inputProcessor.process(bmp.content))
                            .build();

                    for (int index = messages.size() - 1 - bmp.frequency;
                            index > 0; index = index - bmp.frequency) {

                        messages.add(index, bumpMessage);
                    }

                    return messages;
                })
                .orElse(messages);
    }

    public List<String> formatNudge(final Persona persona, final List<String> messages, final StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getNudge())
                .filter(Nudge.isValid)
                .map(ndge -> {
                    messages.add(messages.stream()
                        .filter(m -> !m.startsWith(persona.getName()))
                        .mapToInt(messages::indexOf)
                        .reduce((a, b) -> b)
                        .orElse(0) + 1, inputProcessor.process(ndge.content));

                    return messages;
                })
                .orElse(messages);
    }

    public List<String> formatBump(final Persona persona, final List<String> messages, final StringProcessor inputProcessor) {

        return Optional.ofNullable(persona.getBump())
                .filter(Bump.isValid)
                .map(bmp -> {
                        for (int index = messages.size() - 1 - bmp.frequency;
                            index > 0; index = index - bmp.frequency) {

                        messages.add(index, inputProcessor.process(bmp.getContent()));
                    }

                    return messages;
                })
                .orElse(messages);
    }

    public List<String> formatMessages(final List<String> messages, final EventData eventData, final StringProcessor inputProcessor) {

        final Persona persona = eventData.getChannelDefinitions().getChannelConfig().getPersona();
        final String personality = inputProcessor.process(persona.getPersonality());
        messages.add(0, personality);
        List<String> chatMessages = formatNudge(persona, messages, inputProcessor);
        return formatBump(persona, chatMessages, inputProcessor);
    }

    public String stringifyMessages(final List<String> messages) {

        return String.join(StringUtils.LF, messages).trim();
    }
}
