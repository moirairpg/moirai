package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
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
    public EventData generateResponse(final EventData eventData, final CompletionService model) {

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

            final List<String> messages = handleMessageHistory(eventData);
            moderationService.moderate(messages, eventData)
                    .subscribe(inputModeration -> model.generate(messages, eventData)
                    .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                    .subscribe(outputModeration -> {
                        final Message responseMessage = eventData.getChannel().sendMessage(textResponse).complete();
                        eventData.setResponseMessage(responseMessage);
                    })));
        }

        return eventData;
    }

    /**
     * Formats last messages history to give the AI context on the adventure
     * @param eventData Object containing the event's important data to be processed
     * @return The list of messages for history
     */
    private List<String> handleMessageHistory(final EventData eventData) {

        LOGGER.debug("Entered message history handling for RPG");
        final ModelSettings modelSettings = eventData.getBotChannelDefinitions().getChannelConfig().getSettings().getModelSettings();
        final MessageChannelUnion channel = eventData.getChannel();
        final SelfUser bot = eventData.getBot();
        return channel.getHistory()
                .retrievePast(modelSettings.getChatHistoryMemory()).complete()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .map(m -> MessageFormat.format("{0} said: {1}",
                        m.getAuthor().getName(), m.getContentDisplay().trim()))
                .sorted(Collections.reverseOrder())
                .toList();
    }
}
