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
import es.thalesalv.gptbot.adapters.data.db.entity.CharacterProfileEntity;
import es.thalesalv.gptbot.adapters.data.db.entity.CharacterRegexEntity;
import es.thalesalv.gptbot.adapters.data.db.repository.CharacterProfileRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.CharacterRegexRepository;
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
    private final CharacterProfileRepository characterProfileRepository;
    private final CharacterRegexRepository characterRegexRepository;

    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";
    private static final String CHARACTER_DESCRIPTION = "{0}''s description is: {1}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonMasterUseCase.class);

    @Override
    public void generateResponse(SelfUser bot, User player, Message message, MessageChannelUnion channel, final Mentions mentions, final GptModel model) {

        LOGGER.debug("Entered generation of response for RPG");
        if (mentions.isMentioned(bot, Message.MentionType.USER)) {
            channel.sendTyping().complete();
            final List<String> messages = new ArrayList<>();
            final Set<CharacterProfileEntity> charactersFound = new HashSet<>();

            handleMessageHistory(messages, bot, channel);
            handlePlayerCharacters(charactersFound, messages, player, mentions);
            handleCharactersMentioned(messages, charactersFound);

            charactersFound.stream().forEach(character -> {
                messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, character.getName()));
                messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, character.getName(), character.getDescription()));
                Optional.ofNullable(character.getPlayerDiscordId()).ifPresent(id -> {
                    final User p = jda.retrieveUserById(id).complete();
                    messages.replaceAll(m -> m.replaceAll(p.getAsTag(), character.getName())
                            .replaceAll("(@|)" + p.getName(), character.getName()));
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
     * Extracts characters from database given the player's Discord user ID
     * @param messages List of messages in the channel
     * @param player Player user
     * @param mentions Mentioned users (their characters are extracted too)
     */
    private void handlePlayerCharacters(Set<CharacterProfileEntity> charactersFound, List<String> messages, User player, Mentions mentions) {

        LOGGER.debug("Entered player character handling");
        characterProfileRepository.findByPlayerDiscordId(player.getId())
                .ifPresent(characterProfile -> {
                    charactersFound.add(characterProfile);
                    messages.replaceAll(m -> m.replaceAll(player.getAsTag(), characterProfile.getName())
                            .replaceAll("(@|)" + player.getName(), characterProfile.getName()));
                });

        mentions.getUsers().stream()
                .forEach(mention -> characterProfileRepository.findByPlayerDiscordId(mention.getId())
                        .ifPresent(characterProfile -> {
                            charactersFound.add(characterProfile);
                            messages.replaceAll(m -> m.replaceAll(mention.getAsTag(), characterProfile.getName())
                                    .replaceAll("(@|)" + mention.getName(), characterProfile.getName()));
                        }));
    }

    /**
     * Extracts character profiles from the conversation when their Discord user is not mentioned
     * @param messages List of messages in the channel
     * @param charactersFound List of characters found in the messages until now
     */
    private void handleCharactersMentioned(List<String> messageList, Set<CharacterProfileEntity> charactersFound) {

        LOGGER.debug("Entered mentioned characters handling");
        final String messages = messageList.stream().collect(Collectors.joining("\n"));
        List<CharacterRegexEntity> charRegex = characterRegexRepository.findAll();
        charRegex.forEach(c -> {
            Pattern p = Pattern.compile(Pattern.quote(c.getRegex()));
            Matcher matcher = p.matcher(messages);
            if (matcher.find()) {
                characterProfileRepository.findById(c.getCharacterProfile().getId()).ifPresent(charactersFound::add);
            }
        });
    }

    private String formatAdventureForPrompt(List<String> messages, SelfUser bot) {

        LOGGER.debug("Entered RPG conversation formatter");
        messages.replaceAll(message -> message.replaceAll("@" + bot.getName(), StringUtils.EMPTY).trim());
        return messages.stream().collect(Collectors.joining("\n")).trim()
                .replace(bot.getName() + " (ID " + bot.getId() + ")", "Dungeon Master")
                .replace(bot.getName(), "Dungeon Master").trim();
    }
}
