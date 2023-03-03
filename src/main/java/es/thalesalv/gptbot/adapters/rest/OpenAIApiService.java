package es.thalesalv.gptbot.adapters.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.exception.OpenAiApiException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationRequest;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import reactor.core.publisher.Mono;

@Service
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    private final WebClient webClient;

    private static final String BEARER = "Bearer ";
    private static final String OPENAI_API_BASE_URL = "https://api.openai.com";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);
    
    public OpenAIApiService() {

        this.webClient = WebClient
                .builder()
                .baseUrl(OPENAI_API_BASE_URL)
                .build();
    }

    public Mono<GptResponse> callGptChatApi(final GptRequest request) {

        LOGGER.debug("Making request to OpenAI GPT API -> {}", request);
        return webClient.post()
                .uri("/v1/chat/completions")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .bodyToMono(GptResponse.class)
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
        return webClient.post()
                .uri("/v1/completions")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + openAiToken);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, this::handle4xxError)
                .bodyToMono(GptResponse.class)
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

    private Mono<Throwable> handle4xxError(final ClientResponse clientResponse) {

        LOGGER.debug("Exception caught while calling OpenAI API");
        return clientResponse.bodyToMono(GptResponse.class)
            .map(errorResponse -> {
                LOGGER.error("Error while calling OpenAI API. Message -> {}", errorResponse.getError().getMessage());
                return new OpenAiApiException("Error while calling OpenAI API.", errorResponse);
            });
    }
}
