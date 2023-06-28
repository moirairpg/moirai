package es.thalesalv.chatrpg.application.service.usecases;

import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationFeedbackService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.DelayedPredicate;
import es.thalesalv.chatrpg.application.util.StringProcessors;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatUseCase implements BotUseCase {

    private final ModerationService moderationService;
    private final ModerationFeedbackService moderationFeedbackService;

    private static final String STOP_MEMORY_EMOJI = "chatrpg_stop";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatUseCase.class);

    @Override
    public void generateResponse(final EventData eventData, final CompletionService model) {

        LOGGER.debug("Entered generation for normal text.");
        eventData.getCurrentChannel()
                .sendTyping()
                .complete();

        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        if (message.getContentRaw()
                .trim()
                .equals(bot.getAsMention()
                        .trim())) {
            message.delete()
                    .submit()
                    .whenComplete((d, e) -> {
                        if (e != null) {
                            LOGGER.error("Error deleting trigger mention in RPG", e);
                            throw new DiscordFunctionException("Error deleting trigger mention in RPG", e);
                        }
                    });
        }
        final List<String> messages = handleHistory(eventData);
        moderationService.moderateInput(messages, eventData)
                .subscribe(inputModeration -> model.generate(messages, eventData)
                        .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                                .subscribe(outputModeration -> {
                                    eventData.setInputModerationResult(inputModeration.getModerationResult()
                                            .get(0));

                                    eventData.setOutputModerationResult(outputModeration.getModerationResult()
                                            .get(0));

                                    moderationFeedbackService.sendModerationFeedback(eventData);

                                    eventData.getCurrentChannel()
                                            .sendMessage(textResponse)
                                            .complete();
                                })));
    }

    /**
     * Formats last messages history including reply reference to give the AI
     * context on the past conversation
     *
     * @param eventData Object containing the event's important data to be processed
     * @return The processed list of messages
     */
    private List<String> handleHistory(final EventData eventData) {

        final List<String> formattedReplies = getFormattedReplies(eventData);
        final Predicate<Message> stopFilter = stopFilter(eventData);
        final Predicate<Message> skipFilter = skipFilter(eventData);
        List<String> messages = getHistory(eventData).stream()
                .filter(skipFilter)
                .takeWhile(stopFilter.negate())
                .map(StringProcessors.chatFormatter())
                .collect(Collectors.toList());

        Collections.reverse(messages);
        messages.addAll(formattedReplies);
        return messages;
    }

    private Predicate<Message> stopFilter(final EventData eventData) {

        final SelfUser bot = eventData.getBot();
        final Predicate<Message> isBotTagged = DelayedPredicate.withTest(m -> m.getContentRaw()
                .contains(bot.getAsMention()));

        final Predicate<Message> hasStopReaction = message -> message.getReactions()
                .stream()
                .anyMatch(r -> STOP_MEMORY_EMOJI.equals(r.getEmoji()
                        .getName()));

        return isBotTagged.or(hasStopReaction);
    }

    private Predicate<Message> skipFilter(final EventData eventData) {

        final SelfUser bot = eventData.getBot();
        return m -> !m.getContentRaw()
                .trim()
                .equals(bot.getAsMention()
                        .trim());
    }

    private List<String> getFormattedReplies(final EventData eventData) {

        if (null == eventData.getMessage()
                .getReferencedMessage()) {
            return Collections.emptyList();
        } else {
            String formattedReference = StringProcessors.formattedReference()
                    .apply(eventData.getMessage());
            String formattedReply = StringProcessors.formattedResponse()
                    .apply(eventData.getMessage());
            return Arrays.asList(formattedReference, formattedReply);
        }
    }

    private List<Message> getHistory(final EventData eventData) {

        final MessageChannelUnion channel = eventData.getCurrentChannel();
        final Message repliedMessage = eventData.getMessage()
                .getReferencedMessage();

        final int historySize = eventData.getChannelDefinitions()
                .getChannelConfig()
                .getModelSettings()
                .getChatHistoryMemory();

        if (null == repliedMessage) {
            return channel.getHistory()
                    .retrievePast(historySize)
                    .complete();
        } else {
            return channel.getHistoryBefore(repliedMessage, historySize)
                    .complete()
                    .getRetrievedHistory();
        }
    }
}
