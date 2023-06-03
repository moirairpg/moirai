package es.thalesalv.chatrpg.adapters.rest.client;

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
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import reactor.core.publisher.Mono;

@Service
public class ChatApiService implements CompletionApiService<ChatCompletionRequest> {

    @Value("${chatrpg.openai.api-token}")
    private String openAiToken;

    @Value("${chatrpg.openai.chat-completions-uri}")
    private String chatCompletionsUri;

    private final WebClient webClient;
    private final CommonErrorHandler commonErrorHandler;

    private static final String BEARER = "Bearer ";
    private static final String BOT_RESPONSE_ERROR = "Bot response contains an error";
    private static final String RECEIVED_MODEL_RESPONSE = "Received response from OpenAI GPT API -> {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatApiService.class);

    public ChatApiService(@Value("${chatrpg.openai.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder, final CommonErrorHandler commonErrorHandler) {

        this.commonErrorHandler = commonErrorHandler;
        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl)
                .build();
    }

    @Override
    public Mono<CompletionResponse> callCompletion(ChatCompletionRequest request, EventData eventData) {

        LOGGER.info("Making request to OpenAI chat completions API -> {}", request);
        return webClient.post()
                .uri(chatCompletionsUri)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, e -> commonErrorHandler.handle4xxError(e, eventData))
                .bodyToMono(CompletionResponse.class)
                .map(response -> {
                    LOGGER.info(RECEIVED_MODEL_RESPONSE, response);
                    if (response.getError() != null) {
                        LOGGER.error(BOT_RESPONSE_ERROR, response.getError());
                        throw new ErrorBotResponseException(BOT_RESPONSE_ERROR, response);
                    }

                    return response;
                })
                .doOnError(ErrorBotResponseException.class::isInstance,
                        e -> commonErrorHandler.handleResponseError(eventData));
    }
}
