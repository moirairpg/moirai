package es.thalesalv.chatrpg.application.service.helper;

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

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;

@Component
@RequiredArgsConstructor
public class MessageFormatHelper {

    private static final String ROLE_SYSTEM = "system";
    private static final String ROLE_ASSISTANT = "assistant";
    private static final String ROLE_USER = "user";
    private static final String CHARACTER_DESCRIPTION = "{0} description: {1}";
    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";

    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFormatHelper.class);

    /**
     * Extracts player characters entries from database given the player's Discord user ID
     * @param entriesFound List of entries found in the messages until now
     * @param messages List of messages in the channel
     * @param player Player user
     * @param mentions Mentioned users (their characters are extracted too)
     */
    public void handlePlayerCharacterEntries(final Set<LorebookEntry> entriesFound, final List<String> messages, final User player, final Mentions mentions) {

        LOGGER.debug("Entered player character entry handling");
        lorebookRepository.findByPlayerDiscordId(player.getId())
                .ifPresent(entry -> {
                    entriesFound.add(entry);
                    messages.replaceAll(m -> m.replaceAll(player.getAsTag(), entry.getName())
                            .replaceAll("(@|)" + player.getName(), entry.getName()));
                });

        mentions.getUsers().stream()
                .forEach(mention -> lorebookRepository.findByPlayerDiscordId(mention.getId())
                        .ifPresent(entry -> {
                            entriesFound.add(entry);
                            messages.replaceAll(m -> m.replaceAll(mention.getAsTag(), entry.getName())
                                    .replaceAll("(@|)" + mention.getName(), entry.getName()));
                        }));
    }

    /**
     * Extracts lore entries from the conversation when they're mentioned by name
     * @param messages List of messages in the channel
     * @param entriesFound List of entries found in the messages until now
     */
    public void handleEntriesMentioned(final List<String> messageList, final Set<LorebookEntry> entriesFound) {

        LOGGER.debug("Entered mentioned entries handling");
        final String messages = messageList.stream().collect(Collectors.joining("\n"));
        List<LorebookRegex> charRegex = lorebookRegexRepository.findAll();
        charRegex.forEach(e -> {
            Pattern p = Pattern.compile(e.getRegex());
            Matcher matcher = p.matcher(messages);
            if (matcher.find()) {
                lorebookRepository.findById(e.getLorebookEntry().getId()).ifPresent(entriesFound::add);
            }
        });
    }

    public void processEntriesFoundForRpg(final Set<LorebookEntry> entriesFound, final List<String> messages, final JDA jda) {

        entriesFound.stream().forEach(entry -> {
            if (StringUtils.isNotBlank(entry.getPlayerDiscordId())) {
                messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, entry.getName()));
            }

            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription()));
            Optional.ofNullable(entry.getPlayerDiscordId()).ifPresent(id -> {
                final User p = jda.retrieveUserById(id).complete();
                messages.replaceAll(m -> m.replaceAll(p.getAsTag(), entry.getName())
                        .replaceAll("(@|)" + p.getName(), entry.getName()));
            });
        });
    }

    public void processEntriesFoundForChat(final Set<LorebookEntry> entriesFound, final List<String> messages) {

        entriesFound.stream().forEach(entry -> 
            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, entry.getName(), entry.getDescription())));
    }

    public List<ChatGptMessage> formatMessagesForChatGpt(final List<String> messages, final MessageEventData eventData) {

        final Persona persona = eventData.getPersona();
        final SelfUser bot = eventData.getBot();
        final String personality = persona.getPersonality().replace("{0}", persona.getName());
        final List<ChatGptMessage> chatGptMessages = messages.stream()
                .filter(msg -> !msg.trim().equals((bot.getName() + " said:").trim()))
                .map(msg -> ChatGptMessage.builder()
                            .role(determineRole(msg, bot))
                            .content(formatBotName(msg, bot, persona))
                            .build())
                .collect(Collectors.toList());

        chatGptMessages.add(0, ChatGptMessage.builder()
                .role(ROLE_SYSTEM)
                .content(MessageFormat.format(personality, persona.getName()).trim())
                .build());
            
        return chatGptMessages;
    }

    private String formatBotName(final String msg, final SelfUser bot, final Persona persona) {

        return msg.replace(bot.getName() + " said: ", StringUtils.EMPTY)
                .replace("@" + bot.getName(), "@" + persona.getName());
    }

    private String determineRole(final String message, final SelfUser bot) {

        final boolean isChat = message.matches("^(.*) (says|said|quoted|replied).*");
        if (message.startsWith(bot.getName())) {
            return ROLE_ASSISTANT;
        } else if (isChat && !message.startsWith("I will remember to never")) {
            return ROLE_USER;
        }

        return ROLE_SYSTEM;
    }
}
