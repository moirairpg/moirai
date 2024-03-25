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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.exception.AuthenticationFailedException;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordErrorResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordAuthenticationAdapter implements DiscordAuthenticationPort {

    private static final String TOKEN_URI = "/oauth2/token";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public DiscordAuthenticationAdapter(@Value("${chatrpg.discord.api-base-url}") String discordBaseUrl,
            WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(discordBaseUrl).build();
    }

    @Override
    public Mono<DiscordAuthResponse> authenticate(DiscordAuthRequest request) {

        final MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        Map<String, String> fieldMap = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {
        });

        valueMap.setAll(fieldMap);
        return webClient.post()
                .uri(TOKEN_URI)
                .headers(headers -> {
                    headers.add(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);
                    headers.add(HttpHeaders.ACCEPT, CONTENT_TYPE_VALUE);
                })
                .body(BodyInserters.fromFormData(valueMap))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (response) -> response.bodyToMono(DiscordErrorResponse.class)
                                .map(errorResponse -> {
                                    return new AuthenticationFailedException(errorResponse.getErrorDescription(),
                                            "There was an error authenticating the user");
                                }))
                .bodyToMono(DiscordAuthResponse.class);
    }
}
