package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.core.application.model.result.TextModerationResult;
import es.thalesalv.chatrpg.core.application.port.TextModerationPort;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResponse;
import reactor.core.publisher.Mono;

@Component
public class TextModerationAdapter implements TextModerationPort {

    private final String moderationUrl;
    private final WebClient webClient;

    public TextModerationAdapter(@Value("${chatrpg.openai.api.base-url}") String baseUrl,
            @Value("${chatrpg.openai.api.moderation-uri}") String moderationUrl,
            @Value("${chatrpg.openai.api.token}") String token,
            WebClient.Builder webClientBuilder) {

        this.moderationUrl = moderationUrl;
        this.webClient = webClientBuilder.baseUrl(baseUrl)
                .defaultHeaders(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .build();
    }

    @Override
    public Mono<TextModerationResult> moderate(String text) {

        return webClient.post()
                .uri(moderationUrl)
                .bodyValue(text)
                .retrieve()
                .bodyToMono(ModerationResponse.class)
                .map(this::toResult);
    }

    private TextModerationResult toResult(ModerationResponse response) {

        return TextModerationResult.builder()
                .contentFlagged(response.getResults().get(0).getFlagged())
                .flaggedTopics(response.getResults()
                        .get(0)
                        .getCategories()
                        .entrySet()
                        .stream()
                        .filter(this::isTopicFlagged)
                        .map(Entry::getKey)
                        .toList())
                .moderationScores(response.getResults()
                        .get(0)
                        .getCategoryScores()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> Double.valueOf(entry.getValue()))))
                .build();
    }

    private boolean isTopicFlagged(Entry<String, Boolean> entry) {
        return entry.getValue();
    }
}
