package es.thalesalv.gptbot.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.service.interfaces.GptModel;
import es.thalesalv.gptbot.application.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class DungeonMasterUseCase implements BotUseCase {

    private final JDA jda;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";
    private static final String CHARACTER_DESCRIPTION = "{0}''s description is: {1}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonMasterUseCase.class);

    @Override
    public void generateResponse(SelfUser bot, User player, Message message, MessageChannelUnion channel, final Mentions mentions, final GptModel model) {

        LOGGER.debug("Entered generation of response for RPG");
        if (mentions.isMentioned(bot, Message.MentionType.USER)) {
            channel.sendTyping().complete();
            final List<String> messages = new ArrayList<>();
            final Set<LorebookEntry> entriesFound = new HashSet<>();

            handleMessageHistory(messages, bot, channel);
            handlePlayerCharacterEntries(entriesFound, messages, player, mentions);
            handleEntriesMentioned(messages, entriesFound);

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

            MessageUtils.formatPersonality(messages, contextDatastore.getPersona(), bot);
            final String chatifiedMessage = formatAdventureForPrompt(messages, bot);
            final Persona persona = contextDatastore.getPersona();
            moderationService.moderate(chatifiedMessage)
                    .subscribe(moderationResult -> model.generate(chatifiedMessage, persona)
                    .subscribe(textResponse -> channel.sendMessage(textResponse).queue()));
        }
    }
    
    /**
     * Formats last messages history to give the AI context on the adventure
     * @param messages List messages before the one sent
     * @param bot Bot user
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(List<String> messages, SelfUser bot, MessageChannelUnion channel) {
    
        LOGGER.debug("Entered history handling for RPG");
        channel.getHistory()
                .retrievePast(contextDatastore.getPersona().getChatHistoryMemory()).complete()
                .stream()
                .map(m -> {
                    if (m.getContentDisplay().matches(("@" + bot.getName()).trim() + "$")) {
                        channel.deleteMessageById(m.getId()).complete();
                    }

                    return m;
                })
                .filter(m -> !m.getContentDisplay().matches(("@" + bot.getName()).trim() + "$"))
                .forEach(m -> messages.add(MessageFormat.format("{0}: {1}", m.getAuthor().getName(), 
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim())));

        Collections.reverse(messages);
    }

    /**
     * Extracts player characters entries from database given the player's Discord user ID
     * @param entriesFound List of entries found in the messages until now
     * @param messages List of messages in the channel
     * @param player Player user
     * @param mentions Mentioned users (their characters are extracted too)
     */
    private void handlePlayerCharacterEntries(Set<LorebookEntry> entriesFound, List<String> messages, User player, Mentions mentions) {

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
    private void handleEntriesMentioned(List<String> messageList, Set<LorebookEntry> entriesFound) {

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

    private String formatAdventureForPrompt(List<String> messages, SelfUser bot) {

        LOGGER.debug("Entered RPG conversation formatter");
        messages.add("Dungeon Master:");
        messages.replaceAll(message -> message.replaceAll("@" + bot.getName(), StringUtils.EMPTY)
                .replaceAll(bot.getName(), "Dungeon Master").trim());

        return messages.stream().collect(Collectors.joining("\n"));
    }
}
