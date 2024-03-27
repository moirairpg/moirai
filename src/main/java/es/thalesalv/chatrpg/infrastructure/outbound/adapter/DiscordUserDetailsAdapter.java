package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.function.client.WebClient;

import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.core.application.port.DiscordUserDetailsPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordUserDetailsAdapter implements DiscordUserDetailsPort {

    private static final String SELF_PROFILE_ENDPOINT = "/users/@me";
    private static final String AUTHENTICATION_ERROR = "Error authenticating user on discord: %s; detail: %s";

    private final WebClient webClient;

    public DiscordUserDetailsAdapter(@Value("${chatrpg.discord.api-base-url}") String discordBaseUrl,
            WebClient.Builder webClientBuilder) {

        this.webClient = webClientBuilder.baseUrl(discordBaseUrl).build();
    }

    @Override
    public Mono<DiscordUserDataResponse> retrieveLoggedUser(String token) {

        return webClient.get()
                .uri(SELF_PROFILE_ENDPOINT)
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .onStatus(code -> code.isSameCodeAs(HttpStatusCode.valueOf(401)),
                        __ -> Mono.error(new AuthenticationFailedException()))
                .bodyToMono(DiscordUserDataResponse.class);
    }
}
