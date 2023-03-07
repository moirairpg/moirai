package es.thalesalv.gptbot.adapters.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.errorhandling.CommonErrorHandler;
import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.model.openai.gpt.ChatGptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.Gpt3Request;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import reactor.core.publisher.Mono;

@Service
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;
    
    @Value("${config.openai.completions-uri}")
    private String completionsUri;
    
    @Value("${config.openai.chat-completions-uri}")
    private String chatCompletionsUri;

    @Value("${config.openai.moderation-uri}")
    private String moderationUri;

    private final WebClient webClient;
    private final CommonErrorHandler commonErrorHandler;

    private static final String BEARER = "Bearer ";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public OpenAIApiService(@Value("${config.openai.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder, final CommonErrorHandler commonErrorHandler) {

        this.commonErrorHandler = commonErrorHandler;
        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl).build();
    }

    public Mono<GptResponse> callGptChatApi(final ChatGptRequest request, final MessageEventData messageEventData) {

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
                .bodyToMono(GptResponse.class).map(response -> {
                    LOGGER.info("Received response from OpenAI GPT API -> {}", response);
                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                })
                .doOnError(ErrorBotResponseException.class::isInstance, e -> commonErrorHandler.handleResponseError(messageEventData));
    }

    public Mono<GptResponse> callGptApi(final Gpt3Request request, final MessageEventData messageEventData) {

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
                .bodyToMono(GptResponse.class)
                .map(response -> {
                    LOGGER.info("Received response from OpenAI GPT API -> {}", response);
                    response.setPrompt(request.getPrompt());

                    if (response.getError() != null) {
                        LOGGER.error("Bot response contains an error -> {}", response.getError());
                        throw new ErrorBotResponseException("Bot response contains an error", response);
                    }

                    return response;
                })
                .doOnError(ErrorBotResponseException.class::isInstance, e -> commonErrorHandler.handleResponseError(messageEventData));
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
                });
    }
}
