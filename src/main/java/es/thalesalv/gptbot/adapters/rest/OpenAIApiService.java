package es.thalesalv.gptbot.adapters.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
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

    private static final String OPENAI_API_BASE_URL = "https://api.openai.com";
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public OpenAIApiService() {

        this.webClient = WebClient
                .builder()
                .baseUrl(OPENAI_API_BASE_URL)
                .build();
    }

    public Mono<GptResponse> callGptApi(final GptRequest request) {

        LOGGER.debug("Making request to OpenAI GPT API -> {}", request);
        return webClient.post()
                .uri("/v1/completions")
                .headers(headers -> {
                    headers.add("Authorization", "Bearer " + openAiToken);
                    headers.add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
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
                    headers.add("Authorization", "Bearer " + openAiToken);
                    headers.add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(response -> {
                    LOGGER.debug("Received response from OpenAI moderation API -> {}", response);
                    return response;
                });
    }
}
