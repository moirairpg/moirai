package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final String STOP_MEMORY_FLAG = "{stop}";
    private static final String STOP_MEMORY_EMOJI = "chatrpg_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public EventData generateResponse(final EventData eventData, final CompletionService model) {

        LOGGER.debug("Entered generation for normal text.");
        eventData.getChannel().sendTyping().complete();
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        if (message.getContentRaw().trim().equals(bot.getAsMention().trim())) {
            message.delete().submit().whenComplete((d, e) -> {
                if (e != null) {
                    LOGGER.error("Error deleting trigger mention in RPG", e);
                    throw new DiscordFunctionException("Error deleting trigger mention in RPG", e);
                }
            });
        }

        final List<String> messages = Optional.ofNullable(message.getReferencedMessage())
                .map(rm -> handleMessageHistoryForReplies(eventData))
                .orElseGet(() -> handleMessageHistory(eventData));

        moderationService.moderate(messages, eventData)
                .subscribe(inputModeration -> model.generate(messages, eventData)
                .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                .subscribe(outputModeration -> {
                    final Message responseMessage = eventData.getChannel().sendMessage(textResponse).complete();
                    eventData.setResponseMessage(responseMessage);
                })));

        return eventData;
    }

    /**
     * Formats last messages history on replied to give the AI context on the past conversation
     * @param eventData Object containing the event's important data to be processed
     * @return The processed list of messages
     */
    private List<String> handleMessageHistoryForReplies(final EventData eventData) {

        LOGGER.debug("Entered quoted message history handling for chatbot");
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        final Message reply = message.getReferencedMessage();
        final MessageChannelUnion channel = eventData.getChannel();
        final ModelSettings modelSettings = eventData.getBotChannelDefinitions().getChannelConfig().getSettings().getModelSettings();
        final List<String> messages = channel.getHistoryBefore(reply, modelSettings.getChatHistoryMemory())
                .complete()
                .getRetrievedHistory()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .map(m -> {
                    final User mAuthorUser = m.getAuthor();
                    return Optional.ofNullable(checkForContextCap(m.getReactions()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(MessageFormat.format("{0} said: {1}",
                                mAuthorUser.getName(), m.getContentDisplay()).trim());
                })
                .takeWhile(m -> !m.equals(STOP_MEMORY_FLAG))
                .sorted(Collections.reverseOrder())
                .collect(Collectors.toList());

        messages.add(MessageFormat.format("{0} said earlier: {1}",
                reply.getAuthor().getName(), reply.getContentDisplay()));

        messages.add(MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                eventData.getMessageAuthor().getName(), reply.getAuthor().getName(), message.getContentDisplay()));

        return messages;
    }

    /**
     * Formats last messages history to give the AI context on the current conversation
     * @param eventData Object containing the event's important data to be processed
     * @return The processed list of messages
     */
    private List<String> handleMessageHistory(final EventData eventData) {

        LOGGER.debug("Entered message history handling for chatbot");
        final ModelSettings modelSettings = eventData.getBotChannelDefinitions().getChannelConfig().getSettings().getModelSettings();
        final MessageChannelUnion channel = eventData.getChannel();
        final SelfUser bot = eventData.getBot();
        List<String> messages = channel.getHistory()
                .retrievePast(modelSettings.getChatHistoryMemory()).complete()
                .stream()
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .map(m -> {
                    final User mAuthorUser = m.getAuthor();
                    return Optional.ofNullable(checkForContextCap(m.getReactions()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(MessageFormat.format("{0} said: {1}",
                                mAuthorUser.getName(), m.getContentDisplay().trim()));
                })
                .takeWhile(m -> !m.equals(STOP_MEMORY_FLAG))
                .collect(Collectors.toList());

        Collections.reverse(messages);
        return messages;
    }

    private String checkForContextCap(List<MessageReaction> reactions) {

        for (MessageReaction reaction : reactions) {
            if (reaction.getEmoji().getName().equals(STOP_MEMORY_EMOJI)) {
                return STOP_MEMORY_FLAG;
            }
        }

        return StringUtils.EMPTY;
    }
}
