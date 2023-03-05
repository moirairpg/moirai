package es.thalesalv.gptbot.application.errorhandling;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.domain.exception.OpenAiApiException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CommonErrorHandler {

    private final ContextDatastore contextDatastore;
    private final JDA jda;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonErrorHandler.class);
    private static final String EMPTY_RESPONSE = "The model did not generate an output due to a problem. Please try again. Your message will be removed.\n**Message content:** {0}";
    private static final String UNKNOWN_ERROR = "An error occured and the message could not be sent to the model. Your message will be removed. Please try again.\n**Message content:** {0}";
    private static final String MESSAGE_TOO_LONG = "The message you sent or the total context exceeds the maximum number of available tokens. Send a smaller message or contact the administrator. Your message will be removed.\n**Message content:** {0}";
    
    public void handleEmptyResponse(final MessageEventData messageEventData) {

        final Message message = createMessage();
        notifyUser(EMPTY_RESPONSE, message);
        message.delete().complete();
    }

    public Message createMessage() {

        return jda.getTextChannelById(contextDatastore.getMessageEventData().getChannelId())
                .retrieveMessageById(contextDatastore.getMessageEventData().getMessageId()).complete();
    }

    public void notifyUser(final String notification, final Message message) {

        jda.getUserById(contextDatastore.getMessageEventData().getMessageAuthorId()).openPrivateChannel()
                .complete().sendMessage(MessageFormat.format(notification, message.getContentRaw())).complete();
    }

    public Mono<Throwable> handle4xxError(final ClientResponse clientResponse, final MessageEventData messageEventData) {

        LOGGER.debug("Exception caught while calling OpenAI API");
        return clientResponse.bodyToMono(GptResponse.class)
            .map(errorResponse -> {
                LOGGER.error("Error while calling OpenAI API. Message -> {}", errorResponse.getError().getMessage());
                final Message message = createMessage();
                notifyUser(MESSAGE_TOO_LONG, message);
                message.delete().complete();
                return new OpenAiApiException("Error while calling OpenAI API.", errorResponse);
            });
    }

    public void handleResponseError(final MessageEventData messageEventData) {

        final Message message = createMessage();
        notifyUser(UNKNOWN_ERROR, message);
        message.delete().complete();
    }
}
