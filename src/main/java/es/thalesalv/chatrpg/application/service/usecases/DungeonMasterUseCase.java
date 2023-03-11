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
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
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
    public void generateResponse(final MessageEventData eventData, final GptModelService model) {

        LOGGER.debug("Entered generation of response for RPG. eventData -> {}", eventData);

        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        final Mentions mentions = message.getMentions();
        if (mentions.isMentioned(eventData.getBot(), Message.MentionType.USER)) {
            eventData.getChannel().sendTyping().complete();
            if (message.getContentRaw().trim().equals(bot.getAsMention().trim())) {
                message.delete().submit().whenComplete((d, e) -> {
                    if (e != null) {
                        LOGGER.error("Error deleting trigger mention in RPG", e);
                        throw new DiscordFunctionException("Error deleting trigger mention in RPG", e);
                    }
                });
            }

            final List<String> messages = new ArrayList<>();
            handleMessageHistory(eventData, messages);

            final String chatifiedMessage = formatAdventureForPrompt(messages, eventData.getPersona());
            moderationService.moderate(chatifiedMessage, eventData)
                    .subscribe(moderationResult -> model.generate(chatifiedMessage, messages, eventData)
                    .subscribe(textResponse -> eventData.getChannel().sendMessage(textResponse).queue()));
        }
    }
    
    /**
     * Formats last messages history to give the AI context on the adventure
     * @param persona Persona with current bot settings
     * @param messages List messages before the one sent
     * @param bot Bot user
     * @param channel Channel where the conversation is happening
     */
    private void handleMessageHistory(final MessageEventData eventData, final List<String> messages) {

        LOGGER.debug("Entered message history handling for RPG");
        final Persona persona = eventData.getPersona();
        final MessageChannelUnion channel = eventData.getChannel();
        final SelfUser bot = eventData.getBot();
        channel.getHistory()
                .retrievePast(persona.getChatHistoryMemory()).complete()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .forEach(m -> messages.add(MessageFormat.format("{0} said: {1}", m.getAuthor().getName(), 
                            m.getContentDisplay().replaceAll("(@|)" + persona.getName(), StringUtils.EMPTY).trim())));

        Collections.reverse(messages);
    }

    private String formatAdventureForPrompt(final List<String> messages, final Persona persona) {

        LOGGER.debug("Entered RPG conversation formatter");
        messages.replaceAll(message -> message.replaceAll("@" + persona.getName(), StringUtils.EMPTY));

        return String.join("\n", messages);
    }
}
