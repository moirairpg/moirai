package es.thalesalv.gptbot.adapters.rest;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.exception.OpenAiApiException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import reactor.core.publisher.Mono;

@Service
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    private final JDA jda;
    private final WebClient webClient;
    private final ContextDatastore contextDatastore;

    private static final String BEARER = "Bearer ";
    private static final String OPENAI_API_BASE_URL = "https://api.openai.com";
    private static final String UNKNOWN_ERROR = "An error occured and the message could not be sent to the model. Your message will be removed. Please try again.\n**Message content:** {0}";
    private static final String EMPTY_RESPONSE = "The model did not generate an output due to a problem. Please try again. Your message will be removed.\n**Message content:** {0}";
    private static final String MESSAGE_TOO_LONG = "The message you sent or the total context exceeds the maximum number of available tokens. Send a smaller message or contact the administrator. Your message will be removed.\n**Message content:** {0}";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public OpenAIApiService(final ContextDatastore contextDatastore, final JDA jda) {

        this.jda = jda;
        this.contextDatastore = contextDatastore;
        this.webClient = WebClient
                .builder()
                .baseUrl(OPENAI_API_BASE_URL)
                .build();
    }

    public Mono<GptResponse> callGptChatApi(final GptRequest request) {

        LOGGER.debug("Making request to OpenAI ChatGPT API -> {}", request);
        final MessageEventData messageEventData = contextDatastore.getMessageEventData();
        return webClient.post()
                .uri("/v1/chat/completions")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> handle4xxError(e, messageEventData))
                .bodyToMono(GptResponse.class)
                .doOnError(ErrorBotResponseException.class::isInstance, e -> handleResponseError(messageEventData))
                .doOnError(ModelResponseBlankException.class::isInstance, e -> handleEmptyResponse(messageEventData))
                .map(response -> {
                    LOGGER.debug("Received response from OpenAI GPT API -> {}", response);
                    response.setPrompt(request.getPrompt());

                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                });
    }

    public Mono<GptResponse> callGptApi(final GptRequest request) {

        LOGGER.debug("Making request to OpenAI GPT API -> {}", request);
        final MessageEventData messageEventData = contextDatastore.getMessageEventData();
        return webClient.post()
                .uri("/v1/completions")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> handle4xxError(e, messageEventData))
                .bodyToMono(GptResponse.class)
                .doOnError(ErrorBotResponseException.class::isInstance, e -> handleResponseError(messageEventData))
                .doOnError(ModelResponseBlankException.class::isInstance, e -> handleEmptyResponse(messageEventData))
                .map(response -> {
                    LOGGER.debug("Received response from OpenAI GPT API -> {}", response);
                    response.setPrompt(request.getPrompt());

                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                });
    }

    public Mono<ModerationResponse> callModerationApi(final ModerationRequest request) {

        LOGGER.debug("Making request to OpenAI moderation API -> {}", request);
        return webClient.post()
                .uri("/v1/moderations")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(response -> {
                    LOGGER.debug("Received response from OpenAI moderation API -> {}", response);
                    return response;
                });
    }

    private Mono<Throwable> handle4xxError(final ClientResponse clientResponse, final MessageEventData messageEventData) {

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

    private void handleEmptyResponse(final MessageEventData messageEventData) {

        final Message message = createMessage();
        notifyUser(EMPTY_RESPONSE, message);
        message.delete().complete();
    }

    private void handleResponseError(final MessageEventData messageEventData) {

        final Message message = createMessage();
        notifyUser(UNKNOWN_ERROR, message);
        message.delete().complete();
    }

    private Message createMessage() {

        return jda.getTextChannelById(contextDatastore.getMessageEventData().getChannelId())
                .retrieveMessageById(contextDatastore.getMessageEventData().getMessageId()).complete();
    }

    private void notifyUser(final String notification, final Message message) {

        jda.getUserById(contextDatastore.getMessageEventData().getMessageAuthorId()).openPrivateChannel()
                .complete().sendMessage(MessageFormat.format(notification, message.getContentRaw())).complete();
    }
}
