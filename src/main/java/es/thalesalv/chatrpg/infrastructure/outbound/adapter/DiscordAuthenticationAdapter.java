package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordAuthenticationAdapter implements DiscordAuthenticationPort {

    private static final String TOKEN_URI = "/oauth2/token";
    private static final String TOKEN_REVOKE_URI = "/oauth2/token/revoke";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String AUTHENTICATION_ERROR = "Error authenticating user on discord: %s; detail: %s";

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public DiscordAuthenticationAdapter(@Value("${chatrpg.discord.api-base-url}") String discordBaseUrl,
            WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(discordBaseUrl).build();
    }

    @Override
    public Mono<DiscordAuthResponse> authenticate(DiscordAuthRequest request) {

        return discordWebClient(TOKEN_URI, request)
                .bodyToMono(DiscordAuthResponse.class);
    }

    @Override
    public Mono<Void> logout(DiscordTokenRevocationRequest request) {

        return discordWebClient(TOKEN_REVOKE_URI, request)
                .bodyToMono(Void.class);
    }

    private ResponseSpec discordWebClient(String url, Object request) {

        final MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        Map<String, String> fieldMap = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {
        });

        valueMap.setAll(fieldMap);
        return webClient.post()
                .uri(url)
                .headers(headers -> {
                    headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);
                    headers.add(HttpHeaders.ACCEPT, CONTENT_TYPE_VALUE);
                })
                .body(BodyInserters.fromFormData(valueMap))
                .retrieve()
                .onStatus(code -> code.isSameCodeAs(HttpStatusCode.valueOf(401)),
                        __ -> Mono.error(new AuthenticationFailedException()));
    }
}
