package es.thalesalv.chatrpg.adapters.rest.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.domain.model.DiscordUserData;
import reactor.core.publisher.Mono;

@Service
public class DiscordApiService {

    private final WebClient webClient;

    public DiscordApiService(@Value("${chatrpg.discord.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl)
                .build();
    }

    public Mono<DiscordUserData> retrieveLoggedUser(final String authorization) {

        return webClient.get()
                .uri("/users/@me")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, authorization);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(DiscordUserData.class);
    }
}
