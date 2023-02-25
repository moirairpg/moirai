package es.thalesalv.gptbot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.model.GptRequestEntity;
import es.thalesalv.gptbot.model.GptResponseEntity;
import reactor.core.publisher.Mono;

@Service
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    @Value("${config.openai.settings.temperature}")
    private double temperature;

    @Value("${config.openai.settings.max-tokens}")
    private int maxTokens;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public Mono<GptResponseEntity> callGptApi(String prompt, String model) {

        return WebClient.builder()
                .baseUrl("https://api.openai.com")
                .build()
                .post()
                .uri("/v1/completions")
                .headers(headers -> {
                    headers.add("Authorization", "Bearer " + openAiToken);
                    headers.add("Content-Type", MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .bodyValue(GptRequestEntity.builder()
                        .model(model)
                        .prompt(prompt)
                        .temperature(temperature)
                        .maxTokens(maxTokens)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleClientError)
                .bodyToMono(GptResponseEntity.class);
    }

    private Mono<? extends Throwable> handleClientError(ClientResponse response) {
        
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    LOGGER.info("GPT error response -> {}", response);
                    return Mono.error(new RuntimeException("Error: " + body));
                });
    }
}
