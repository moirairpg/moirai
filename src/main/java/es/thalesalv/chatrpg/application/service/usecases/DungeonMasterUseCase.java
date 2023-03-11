package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class DungeonMasterUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(DungeonMasterUseCase.class);

    @Override
    public void generateResponse(final MessageEventData messageEventData, final Mentions mentions, final GptModelService model) {

        LOGGER.debug("Entered generation of response for RPG");
        if (mentions.isMentioned(messageEventData.getBot(), Message.MentionType.USER)) {
            messageEventData.getChannel().sendTyping().complete();
            final Persona persona = messageEventData.getPersona();
            final List<String> messages = new ArrayList<>();

            handleMessageHistory(persona, messages, messageEventData.getBot(), messageEventData.getChannel());

            final String chatifiedMessage = formatAdventureForPrompt(messages, messageEventData.getBot());
            moderationService.moderate(chatifiedMessage, messageEventData)
                    .subscribe(inputModeration -> model.generate(chatifiedMessage, messages, messageEventData)
                    .subscribe(textResponse -> moderationService.moderateOutput(textResponse, messageEventData)
                    .subscribe(outputModeration -> messageEventData.getChannel().sendMessage(textResponse).queue())));
        }
    }
    
    /**
     * Formats last messages history to give the AI context on the adventure
     * @param persona Persona with current bot settings
     * @param messages List messages before the one sent
     * @param bot Bot user
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(final Persona persona, final List<String> messages, final SelfUser bot, final MessageChannelUnion channel) {
    
        LOGGER.debug("Entered history handling for RPG");
        channel.getHistory()
                .retrievePast(persona.getChatHistoryMemory()).complete()
                .stream()
                .map(m -> {
                    if (m.getContentDisplay().matches(("@" + bot.getName()).trim() + "$")) {
                        channel.deleteMessageById(m.getId()).complete();
                    }

                    return m;
                })
                .filter(m -> !m.getContentDisplay().matches(("@" + bot.getName()).trim() + "$"))
                .forEach(m -> messages.add(MessageFormat.format("{0} says: {1}", m.getAuthor().getName(), 
                            m.getContentDisplay().replaceAll("(@|)" + bot.getName(), StringUtils.EMPTY).trim())));

        Collections.reverse(messages);
    }

    private String formatAdventureForPrompt(final List<String> messages, final SelfUser bot) {

        LOGGER.debug("Entered RPG conversation formatter");
        messages.replaceAll(message -> message.replaceAll("@" + bot.getName(), StringUtils.EMPTY)
                .replaceAll(bot.getName(), "Dungeon Master").trim());

        return String.join("\n", messages);
    }
}
