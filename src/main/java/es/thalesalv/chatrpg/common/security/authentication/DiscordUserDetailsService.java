package es.thalesalv.chatrpg.common.security.authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.common.security.authentication.model.DiscordErrorResponse;
import es.thalesalv.chatrpg.common.security.authentication.model.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Service
public class DiscordUserDetailsService implements ReactiveUserDetailsService {

    private final WebClient webClient;

    public DiscordUserDetailsService(@Value("${chatrpg.discord.api-base-url}") final String discordBaseUrl,
            final WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(discordBaseUrl)
                .build();
    }

    @Override
    public Mono<UserDetails> findByUsername(String token) {

        return retrieveLoggedUser(token).map(userDetails -> {
            return DiscordPrincipal.builder().id(userDetails.getId())
                    .username(userDetails.getUsername())
                    .email(userDetails.getEmail())
                    .build();
        });
    }

    private Mono<DiscordUserDataResponse> retrieveLoggedUser(String token) {

        return webClient.get()
                .uri("/users/@me")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> response.bodyToMono(DiscordErrorResponse.class)
                        .map(errorResponse -> new RuntimeException(
                                "Error authenticating user on discord: " + errorResponse.getError() + "; detail: "
                                        + errorResponse.getErrorDescription())))
                .bodyToMono(DiscordUserDataResponse.class);
    }
}
