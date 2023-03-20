package es.thalesalv.chatrpg.adapters.rest;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.application.errorhandling.CommonErrorHandler;
import es.thalesalv.chatrpg.domain.exception.ErrorBotResponseException;
import es.thalesalv.chatrpg.domain.exception.ModerationException;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.chatrpg.domain.model.openai.moderation.ModerationResponse;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class OpenAIApiService {

    @Value("${chatrpg.openai.api-token}")
    private String openAiToken;

    @Value("${chatrpg.openai.completions-uri}")
    private String completionsUri;

    @Value("${chatrpg.openai.chat-completions-uri}")
    private String chatCompletionsUri;

    @Value("${chatrpg.openai.moderation-uri}")
    private String moderationUri;

    @Value("${chatrpg.discord.retry.error-attempts}")
    private int errorAttemps;

    @Value("${chatrpg.discord.retry.error-delay}")
    private int errorDelay;

    @Value("${chatrpg.discord.retry.moderation-attempts}")
    private int moderationAttempts;

    @Value("${chatrpg.discord.retry.moderation-delay}")
    private int moderationDelay;

    private final WebClient webClient;
    private final CommonErrorHandler commonErrorHandler;

    private static final String BEARER = "Bearer ";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public OpenAIApiService(@Value("${chatrpg.openai.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder, final CommonErrorHandler commonErrorHandler) {

        this.commonErrorHandler = commonErrorHandler;
        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl).build();
    }

    public Mono<CompletionResponse> callGptChatApi(final ChatCompletionRequest request, final MessageEventData messageEventData) {

        LOGGER.info("Making request to OpenAI ChatGPT API -> {}", request);
        return webClient.post()
                .uri(chatCompletionsUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> commonErrorHandler.handle4xxError(e, messageEventData))
                .bodyToMono(CompletionResponse.class)
                .map(response -> {
                    LOGGER.info("Received response from OpenAI GPT API -> {}", response);
                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                })
                .doOnError(ErrorBotResponseException.class::isInstance, e -> commonErrorHandler.handleResponseError(messageEventData))
                .retryWhen(Retry.fixedDelay(moderationAttempts, Duration.ofSeconds(moderationDelay))
                        .filter(ModerationException.class::isInstance))
                .retryWhen(Retry.fixedDelay(errorAttemps, Duration.ofSeconds(errorDelay)));
    }

    public Mono<CompletionResponse> callGptApi(final TextCompletionRequest request, final MessageEventData messageEventData) {

        LOGGER.info("Making request to OpenAI GPT API -> {}", request);
        return webClient.post()
                .uri(completionsUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> commonErrorHandler.handle4xxError(e, messageEventData))
                .bodyToMono(CompletionResponse.class)
                .map(response -> {
                    LOGGER.info("Received response from OpenAI GPT API -> {}", response);
                    response.setPrompt(request.getPrompt());

                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                })
                .doOnError(ErrorBotResponseException.class::isInstance, e -> commonErrorHandler.handleResponseError(messageEventData))
                .retryWhen(Retry.fixedDelay(moderationAttempts, Duration.ofSeconds(moderationDelay))
                        .filter(ModerationException.class::isInstance))
                .retryWhen(Retry.fixedDelay(errorAttemps, Duration.ofSeconds(errorDelay)));
    }

    public Mono<ModerationResponse> callModerationApi(final ModerationRequest request) {

        LOGGER.info("Making request to OpenAI moderation API -> {}", request);
        return webClient.post()
                .uri(moderationUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(response -> {
                    LOGGER.info("Received response from OpenAI moderation API -> {}", response);
                    return response;
                })
                .retryWhen(Retry.fixedDelay(errorAttemps, Duration.ofSeconds(errorDelay)));
    }
}
