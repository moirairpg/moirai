package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.Map;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.exception.DiscordApiException;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordErrorResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordAuthenticationAdapter implements DiscordAuthenticationPort {

    private static final String TOKEN_URI = "/oauth2/token";
    private static final String TOKEN_REVOKE_URI = "/oauth2/token/revoke";
    private static final String USERS_BASE_URI = "/users/%s";

    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String AUTHENTICATION_ERROR = "Error authenticating user on Discord";
    private static final String UNKNOWN_ERROR = "Error on Discord API";

    private static final Predicate<HttpStatusCode> BAD_REQUEST = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(400));

    private static final Predicate<HttpStatusCode> UNAUTHORIZED = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(401));

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
    public Mono<DiscordUserDataResponse> retrieveLoggedUser(String token) {

        return webClient.get()
                .uri(String.format(USERS_BASE_URI, "@me"))
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, token);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .onStatus(UNAUTHORIZED, this::handleUnauthorized)
                .onStatus(BAD_REQUEST, this::handleBadRequest)
                .onStatus(HttpStatusCode::isError, this::handleUnknownError)
                .bodyToMono(DiscordUserDataResponse.class);
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
                .onStatus(UNAUTHORIZED, this::handleUnauthorized)
                .onStatus(BAD_REQUEST, this::handleBadRequest)
                .onStatus(HttpStatusCode::isError, this::handleUnknownError);
    }

    private Mono<? extends Throwable> handleUnauthorized(ClientResponse clientResponse) {

        return Mono.error(new DiscordApiException(HttpStatus.UNAUTHORIZED, AUTHENTICATION_ERROR));
    }

    private Mono<? extends Throwable> handleBadRequest(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> new DiscordApiException(HttpStatus.BAD_REQUEST, resp.getError(),
                        resp.getErrorDescription(),
                        String.format(AUTHENTICATION_ERROR, resp.getError(), resp.getErrorDescription())));
    }

    private Mono<? extends Throwable> handleUnknownError(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> new DiscordApiException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getError(),
                        resp.getErrorDescription(),
                        String.format(UNKNOWN_ERROR, resp.getError(), resp.getErrorDescription())));
    }
}
