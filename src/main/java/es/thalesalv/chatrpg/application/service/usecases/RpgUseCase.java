package es.thalesalv.chatrpg.application.service.usecases;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.application.util.StringProcessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@Component
@RequiredArgsConstructor
public class RpgUseCase implements BotUseCase {

    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RpgUseCase.class);

    @Override
    public EventData generateResponse(final EventData eventData, final CompletionService model) {

        LOGGER.debug("Entered generation of response for RPG. eventData -> {}", eventData);
        final Message message = eventData.getMessage();
        final SelfUser bot = eventData.getBot();
        final Mentions mentions = message.getMentions();

        if (mentions.isMentioned(eventData.getBot(), Message.MentionType.USER)) {
            eventData.getCurrentChannel()
                    .sendTyping()
                    .complete();

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
            moderationService.moderate(messages, eventData)
                    .subscribe(inputModeration -> model.generate(messages, eventData)
                            .subscribe(textResponse -> moderationService.moderateOutput(textResponse, eventData)
                                    .subscribe(outputModeration -> {
                                        final Message responseMessage = eventData.getCurrentChannel()
                                                .sendMessage(textResponse)
                                                .complete();
                                        eventData.setResponseMessage(responseMessage);
                                    })));
        }
        return eventData;
    }

    /**
     * Formats last messages history to give the AI context on the adventure
     *
     * @param eventData Object containing the event's important data to be processed
     * @return The list of messages for history
     */
    private List<String> handleHistory(final EventData eventData) {

        LOGGER.debug("Entered message history handling for RPG");
        final List<String> formattedReplies = getFormattedReplies(eventData);
        final Predicate<Message> skipFilter = skipFilter(eventData);
        List<String> messages = getHistory(eventData).stream()
                .filter(skipFilter)
                .map(StringProcessors.chatFormatter())
                .collect(Collectors.toList());

        Collections.reverse(messages);
        messages.addAll(formattedReplies);
        return messages;
    }

    private Predicate<Message> skipFilter(final EventData eventData) {

        final SelfUser bot = eventData.getBot();
        return m -> !m.getContentRaw()
                .trim()
                .equals(bot.getAsMention()
                        .trim());
    }

    private List<String> getFormattedReplies(final EventData eventData) {

        final Message message = eventData.getMessage();
        final Message reply = message.getReferencedMessage();
        if (null == reply) {
            return Collections.emptyList();
        } else {
            String formattedReference = MessageFormat.format("{0} said earlier: {1}", reply.getAuthor()
                    .getName(), reply.getContentDisplay());

            String formattedReply = MessageFormat.format("{0} quoted the message from {1} and replied with: {2}",
                    message.getAuthor()
                            .getName(),
                    reply.getAuthor()
                            .getName(),
                    message.getContentDisplay());

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
