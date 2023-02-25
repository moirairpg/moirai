package es.thalesalv.gptbot.service;

import es.thalesalv.gptbot.data.ContextDatastore;
import es.thalesalv.gptbot.model.gpt.GptRequestEntity;
import es.thalesalv.gptbot.model.gpt.GptResponseEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    private final ContextDatastore contextDatastore;

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
                        .temperature(contextDatastore.getCurrentChannel().getTemperature())
                        .maxTokens(contextDatastore.getCurrentChannel().getMaxTokens())
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
