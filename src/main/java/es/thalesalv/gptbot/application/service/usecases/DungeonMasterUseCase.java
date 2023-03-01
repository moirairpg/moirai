package es.thalesalv.gptbot.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import edu.stanford.nlp.simple.Sentence;
import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.data.db.entity.CharacterProfileEntity;
import es.thalesalv.gptbot.adapters.data.db.repository.CharacterProfileRepository;
import es.thalesalv.gptbot.application.service.GptService;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.util.MessageUtils;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class DungeonMasterUseCase implements BotUseCase {

    private final GptService gptService;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final CharacterProfileRepository characterProfileRepository;

    private static final String RPG_DM_INSTRUCTIONS = "I will remember to never act or speak on behalf of {0}. I will not repeat what {0} just said. I will only describe the world around {0}.";
    private static final String CHARACTER_DESCRIPTION = "{0}''s description is: {1}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonMasterUseCase.class);

    @Override
    public void generateResponse(SelfUser bot, User player, Message message, MessageChannelUnion channel, final Mentions mentions) {

        LOGGER.debug("Entered generation of response for RPG");
        if (mentions.isMentioned(bot, Message.MentionType.USER)) {
            channel.sendTyping().complete();
            final List<String> messages = new ArrayList<>();

            handleMessageHistory(messages, bot, channel);
            handlePlayerCharacters(messages, player, mentions);
            handleCharactersMentioned(messages, channel);

            MessageUtils.formatPersonality(messages, contextDatastore.getPersona(), bot);
            final String chatifiedMessage = MessageUtils.chatifyMessages(bot, messages)
                    .replace(bot.getName() + " (ID " + bot.getId() + ")", "Dungeon Master")
                    .replace(bot.getName(), "Dungeon Master");

            moderationService.moderate(chatifiedMessage).map(moderationResult -> {
                gptService.callDaVinci(chatifiedMessage).map(textResponse -> {
                    channel.sendMessage(textResponse).queue();
                    return textResponse;
                }).subscribe();

                return moderationResult;
            }).subscribe();
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
    private void handlePlayerCharacters(List<String> messages, User player, Mentions mentions) {

        LOGGER.debug("Entered player character handling");
        final CharacterProfileEntity characterProfile = characterProfileRepository.findByPlayerDiscordId(player.getId());
        if (characterProfile != null) {
            messages.replaceAll(m -> {
                return m.replaceAll(player.getAsTag(), characterProfile.getName())
                    .replaceAll("(@|)" + player.getName(), characterProfile.getName());
            });
        }

        mentions.getUsers().stream().forEach(mention -> {
            final CharacterProfileEntity mentionedProfile = characterProfileRepository.findByPlayerDiscordId(mention.getId());
            if (mentionedProfile != null) {
                messages.replaceAll(m -> {
                    return m.replaceAll(mention.getAsTag(), mentionedProfile.getName())
                            .replaceAll("(@|)" + mention.getName(), mentionedProfile.getName());
                });
            }
        });
    }

    /**
     * Extracts character profiles from the conversation when their Discord user is not mentioned
     * @param messages List of messages in the channel
     * @param channel Channel where the conversation is happening
     */
    private void handleCharactersMentioned(List<String> messages, MessageChannelUnion channel) {

        LOGGER.debug("Entered mentioned characters handling");
        final Sentence sentence = new Sentence(messages.stream().collect(Collectors.joining("\n")));
        final HashSet<String> namesMentioned = new HashSet<String>(sentence.mentions());
        final HashSet<CharacterProfileEntity> charactersMentioned = characterProfileRepository.findByNameIn(namesMentioned);
        charactersMentioned.stream().forEach(character -> {
            messages.add(0, MessageFormat.format(RPG_DM_INSTRUCTIONS, character.getName()));
            messages.add(0, MessageFormat.format(CHARACTER_DESCRIPTION, character.getName(), character.getDescription()));

            final User characterPlayer = channel.getJDA().retrieveUserById(character.getPlayerDiscordId()).complete();
            if (characterPlayer != null) {
                messages.replaceAll(m -> m.replaceAll(characterPlayer.getAsTag(), character.getName())
                        .replaceAll("(@|)" + characterPlayer.getName(), character.getName()));
            }
        });
    }
}
