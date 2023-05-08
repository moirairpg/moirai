package es.thalesalv.chatrpg.application.errorhandling;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

import es.thalesalv.chatrpg.domain.exception.OpenAiApiException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommonErrorHandler {

    private static final String EMPTY_RESPONSE = "The model did not generate an output due to a problem. Please try again.\n**Message content:** {0}";
    private static final String UNKNOWN_ERROR = "An error occured and the message could not be sent to the model. Please try again.\n**Message content:** {0}";
    private static final String MESSAGE_TOO_LONG = "The message you sent or the total context contained in this channel exceeds the maximum number of available tokens. Send a smaller message or contact the administrator.";
    private static final String SOMETHING_WRONG = "Something went wrong. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonErrorHandler.class);

    public void handleEmptyResponse(final EventData eventData) {

        notifyUser(EMPTY_RESPONSE, eventData);
    }

    public void notifyUser(final String notification, final EventData eventData) {

        eventData.getCurrentChannel()
                .sendMessage(MessageFormat.format(notification, eventData.getMessage()
                        .getContentRaw()))
                .queue(msg -> msg.delete()
                        .queueAfter(10, TimeUnit.SECONDS));
    }

    public Mono<Throwable> handle4xxError(final ClientResponse clientResponse, final EventData eventData) {

        LOGGER.debug("Exception caught while calling OpenAI API");
        return clientResponse.bodyToMono(CompletionResponse.class)
                .map(errorResponse -> {
                    if (404 == clientResponse.statusCode()
                            .value()) {
                        LOGGER.error("OpenAI API threw NOT FOUND error -> {}", errorResponse.getError()
                                .getMessage());

                        notifyUser(SOMETHING_WRONG, eventData);
                    } else if (429 == clientResponse.statusCode()
                            .value()) {
                        LOGGER.error("OpenAI API is currently overloaded or quota limit has been reached -> {}",
                                errorResponse.getError()
                                        .getMessage());

                        notifyUser(SOMETHING_WRONG, eventData);
                    } else if (400 == clientResponse.statusCode()
                            .value()) {

                        String errorMessage = errorResponse.getError()
                                .getMessage();

                        if (errorMessage.contains("This model's maximum context length")) {
                            LOGGER.error("Maximum token context for model surpassed -> {}", errorResponse.getError()
                                    .getMessage());

                            notifyUser(MESSAGE_TOO_LONG, eventData);
                        } else {
                            LOGGER.error("There was a problem calling OpenAI API. Server return BAD REQUEST -> {}",
                                    errorResponse.getError()
                                            .getMessage());

                            notifyUser(SOMETHING_WRONG, eventData);
                        }
                    } else {
                        LOGGER.error("Error while calling OpenAI API. Message -> {}", errorResponse.getError()
                                .getMessage());

                        notifyUser(SOMETHING_WRONG, eventData);
                    }
                    return new OpenAiApiException("Error while calling OpenAI API.", errorResponse);
                });
    }

    public void handleResponseError(final EventData eventData) {

        notifyUser(UNKNOWN_ERROR, eventData);
    }
}
