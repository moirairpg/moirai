package es.thalesalv.chatrpg.application.service.usecases;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class ChatbotUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final String STOP_MEMORY_EMOJI = "chatrpg_stop";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotUseCase.class);

    @Override
    public MessageEventData generateResponse(final MessageEventData eventData, final CompletionService model) {

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

        final List<String> messages = handleHistory(eventData);

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
     * Formats last messages history including reply reference to give the AI context on the past conversation
     * @param eventData Object containing the event's important data to be processed
     * @return The processed list of messages
     */
    private List<String> handleHistory(final MessageEventData eventData) {
        final List<String> formattedReplies = getFormattedReplies(eventData);
        final Predicate<Message> stopFilter = stopFilter(eventData);
        final Predicate<Message> skipFilter = skipFilter(eventData);

        List<String> messages = getHistory(eventData)
                .stream()
<<<<<<< HEAD
                .filter(skipFilter)
                .takeWhile(stopFilter.negate())
                .map(m -> MessageFormat.format("{0} said: {1}",
                        m.getAuthor().getName(), m.getContentDisplay().trim()))
=======
                .filter(m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim()))
                .map(m -> {
                    final User mAuthorUser = m.getAuthor();
                    return Optional.ofNullable(checkForContextCap(m.getReactions()))
                        .filter(StringUtils::isNotBlank)
                        .orElse(MessageFormat.format("{0} said: {1}",
                                mAuthorUser.getName(), m.getContentDisplay()).trim());
                })
                .takeWhile(m -> !m.equals(STOP_MEMORY_FLAG))
                .collect(Collectors.toList());

        Collections.reverse(messages);

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
    private List<String> handleMessageHistory(final MessageEventData eventData) {

        LOGGER.debug("Entered message history handling for chatbot");
        final ModelSettings modelSettings = eventData.getChannelConfig().getSettings().getModelSettings();
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
>>>>>>> master
                .collect(Collectors.toList());

        Collections.reverse(messages);
        messages.addAll(formattedReplies);

        return messages;
    }

    private List<String> getFormattedReplies(final MessageEventData eventData) {
        final Message message = eventData.getMessage();
        final Message reply = message.getReferencedMessage();
        if (null == reply) {
            return Collections.emptyList();
        } else {
            String formattedReference = MessageFormat.format("{0} said earlier: {1}",
                    reply.getAuthor().getName(), reply.getContentDisplay());
            String formattedReply = MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                    message.getAuthor().getName(), reply.getAuthor().getName(), message.getContentDisplay());
            return Arrays.asList(formattedReference, formattedReply);
        }
    }

    private Predicate<Message> stopFilter(final MessageEventData eventData) {
        final SelfUser bot = eventData.getBot();
        final Predicate<Message> isBotTagged = m -> m.getContentRaw().contains(bot.getAsMention());
        final Predicate<Message> hasStopReaction = message -> message.getReactions().stream().anyMatch(r -> STOP_MEMORY_EMOJI.equals(r.getEmoji().getName()));
        return isBotTagged.or(hasStopReaction);
    }

    private Predicate<Message> skipFilter(final MessageEventData eventData) {
        final SelfUser bot = eventData.getBot();
        return m -> !m.getContentRaw().trim().equals(bot.getAsMention().trim());
    }

    private List<Message> getHistory(final MessageEventData eventData) {
        final MessageChannelUnion channel = eventData.getChannel();
        final Message repliedMessage = eventData.getMessage().getReferencedMessage();
        final int historySize = eventData.getChannelConfig().getSettings().getModelSettings().getChatHistoryMemory();
        if (null == repliedMessage) {
             return channel.getHistory().retrievePast(historySize).complete();
         } else {
             return channel.getHistoryBefore(repliedMessage, historySize).complete().getRetrievedHistory();
         }
    }
}
