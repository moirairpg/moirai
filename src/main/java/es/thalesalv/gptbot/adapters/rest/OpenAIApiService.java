package es.thalesalv.gptbot.adapters.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.domain.model.gpt.GptRequestEntity;
import es.thalesalv.gptbot.domain.model.gpt.GptResponseEntity;
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

    public Mono<GptResponseEntity> callGptApi(final GptRequestEntity request) {

        LOGGER.debug("Making request to GPT API -> {}", request);
        return webClient.post()
                .uri("/v1/completions")
                .headers(headers -> {
                    headers.add("Authorization", "Bearer " + openAiToken);
                    headers.add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GptResponseEntity.class)
                .map(response -> {
                    LOGGER.debug("Received response from GPT API -> {}", response);
                    response.setPrompt(request.getPrompt());
                    return response;
                });
    }
}
