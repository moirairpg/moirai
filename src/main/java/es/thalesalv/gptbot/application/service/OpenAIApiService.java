package es.thalesalv.gptbot.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.domain.model.gpt.GptRequestEntity;
import es.thalesalv.gptbot.domain.model.gpt.GptResponseEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class OpenAIApiService {

    @Value("${config.openai.api-token}")
    private String openAiToken;

    private final ContextDatastore contextDatastore;

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAIApiService.class);

    public Mono<GptResponseEntity> callGptApi(final String prompt, final String model) {

        final int maxTokens = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getMaxTokens() : 100;
        final double temperature = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getTemperature() : 0.6;
        final double presPen = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getPresencePenalty() : 0.2;
        final double freqPen = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getFrequencyPenalty() : 0.2;

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
                        .frequencyPenalty(freqPen)
                        .presencePenalty(presPen)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, this::handleClientError)
                .bodyToMono(GptResponseEntity.class)
                .map(response -> {
                    response.setPrompt(prompt);
                    return response;
                });
    }

    private Mono<? extends Throwable> handleClientError(final ClientResponse response) {
        
        return response.bodyToMono(String.class)
                .flatMap(body -> {
                    LOGGER.info("GPT error response -> {}", response);
                    return Mono.error(new RuntimeException("Error: " + body));
                });
    }
}
