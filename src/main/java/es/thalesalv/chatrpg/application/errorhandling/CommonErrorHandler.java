package es.thalesalv.chatrpg.application.errorhandling;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.exception.OpenAiApiException;
import es.thalesalv.chatrpg.domain.model.openai.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommonErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonErrorHandler.class);
    private static final String EMPTY_RESPONSE = "The model did not generate an output due to a problem. Please try again. Your message will be removed.\n**Message content:** {0}";
    private static final String UNKNOWN_ERROR = "An error occured and the message could not be sent to the model. Your message will be removed. Please try again.\n**Message content:** {0}";
    private static final String MESSAGE_TOO_LONG = "The message you sent or the total context exceeds the maximum number of available tokens. Send a smaller message or contact the administrator. Your message will be removed.\n**Message content:** {0}";
    
    public void handleEmptyResponse(final MessageEventData messageEventData) {

        final Message message = createMessage(messageEventData);
        notifyUser(EMPTY_RESPONSE, message, messageEventData);
        message.delete().complete();
    }

    public Message createMessage(final MessageEventData messageEventData) {

        return messageEventData.getChannel()
                .retrieveMessageById(messageEventData.getMessage().getId()).complete();
    }

    public void notifyUser(final String notification, final Message message, final MessageEventData messageEventData) {

        messageEventData.getMessageAuthor().openPrivateChannel().submit()
                .thenCompose(c -> c.sendMessage(MessageFormat.format(notification, message.getContentRaw())).submit())
                .whenComplete((msg, error) -> {
                    if (error != null) {
                        LOGGER.error("Error sending PM to user", error);
                        throw new DiscordFunctionException("Error sending PM to user", error);
                    }
                });
    }

    public Mono<Throwable> handle4xxError(final ClientResponse clientResponse, final MessageEventData messageEventData) {

        LOGGER.debug("Exception caught while calling OpenAI API");
        return clientResponse.bodyToMono(GptResponse.class)
            .map(errorResponse -> {
                LOGGER.error("Error while calling OpenAI API. Message -> {}", errorResponse.getError().getMessage());
                final Message message = createMessage(messageEventData);
                notifyUser(MESSAGE_TOO_LONG, message, messageEventData);
                message.delete().complete();
                return new OpenAiApiException("Error while calling OpenAI API.", errorResponse);
            });
    }

    public void handleResponseError(final MessageEventData messageEventData) {

        final Message message = createMessage(messageEventData);
        notifyUser(UNKNOWN_ERROR, message, messageEventData);
        message.delete().complete();
    }
}
