package es.thalesalv.chatrpg.application.errorhandling;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

import es.thalesalv.chatrpg.domain.exception.OpenAiApiException;
import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommonErrorHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonErrorHandler.class);
    private static final String EMPTY_RESPONSE = "The model did not generate an output due to a problem. Please try again. Your message will be removed.\n**Message content:** {0}";
    private static final String UNKNOWN_ERROR = "An error occured and the message could not be sent to the model. Your message will be removed. Please try again.\n**Message content:** {0}";
    private static final String MESSAGE_TOO_LONG = "The message you sent or the total context exceeds the maximum number of available tokens. Send a smaller message or contact the administrator. Your message will be removed.\n**Message content:** {0}";
    private static final String SOMETHING_WRONG = "Something went wrong. Please try again.";

    public void handleEmptyResponse(final MessageEventData messageEventData) {

        notifyUser(EMPTY_RESPONSE, messageEventData);
    }

    public void notifyUser(final String notification, final MessageEventData messageEventData) {

        messageEventData.getChannel().sendMessage(MessageFormat.format(notification, messageEventData.getMessage().getContentRaw()))
                .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
    }

    public Mono<Throwable> handle4xxError(final ClientResponse clientResponse, final MessageEventData messageEventData) {

        LOGGER.debug("Exception caught while calling OpenAI API");
        return clientResponse.bodyToMono(CompletionResponse.class)
            .map(errorResponse -> {
                if (404 == clientResponse.statusCode().value()) {
                    LOGGER.error("OpenAI API threw NOT FOUND error -> {}", errorResponse);
                    notifyUser(SOMETHING_WRONG, messageEventData);
                } else {
                    LOGGER.error("Error while calling OpenAI API. Message -> {}", errorResponse.getError().getMessage());
                    notifyUser(MESSAGE_TOO_LONG, messageEventData);
                }

                return new OpenAiApiException("Error while calling OpenAI API.", errorResponse);
            });
    }

    public void handleResponseError(final MessageEventData messageEventData) {

        notifyUser(UNKNOWN_ERROR, messageEventData);
    }
}
