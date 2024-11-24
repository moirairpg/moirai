package me.moirai.discordbot.infrastructure.outbound.adapter.discord;

import java.util.Map;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import me.moirai.discordbot.common.exception.AuthenticationFailedException;
import me.moirai.discordbot.common.exception.DiscordApiException;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.infrastructure.inbound.api.response.DiscordAuthResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.DiscordErrorResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordAuthRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

@Component
public class DiscordAuthenticationAdapter implements DiscordAuthenticationPort {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordAuthenticationAdapter.class);

    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded";
    private static final String AUTHENTICATION_ERROR = "Error authenticating user on Discord";
    private static final String UNKNOWN_ERROR = "Error on Discord API";
    private static final String BAD_REQUEST_ERROR = "Bad request calling Discord API";

    private static final Predicate<HttpStatusCode> BAD_REQUEST = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(400));

    private static final Predicate<HttpStatusCode> UNAUTHORIZED = statusCode -> statusCode
            .isSameCodeAs(HttpStatusCode.valueOf(401));

    private final String usersUri;
    private final String tokenUri;
    private final String tokenRevokeUri;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public DiscordAuthenticationAdapter(@Value("${moirai.discord.api.base-url}") String discordBaseUrl,
            @Value("${moirai.discord.api.users-uri}") String usersUri,
            @Value("${moirai.discord.api.token-uri}") String tokenUri,
            @Value("${moirai.discord.api.token-revoke-uri}") String tokenRevokeUri,
            WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.usersUri = usersUri;
        this.tokenUri = tokenUri;
        this.tokenRevokeUri = tokenRevokeUri;
        this.webClient = webClientBuilder.baseUrl(discordBaseUrl).build();
    }

    @Override
    public Mono<DiscordAuthResponse> authenticate(DiscordAuthRequest request) {

        return discordWebClient(tokenUri, request)
                .bodyToMono(DiscordAuthResponse.class);
    }

    @Override
    public Mono<DiscordUserDataResponse> retrieveLoggedUser(String token) {

        return webClient.get()
                .uri(String.format(usersUri, "@me"))
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

        return discordWebClient(tokenRevokeUri, request)
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

        return Mono.error(new AuthenticationFailedException(AUTHENTICATION_ERROR, AUTHENTICATION_ERROR));
    }

    private Mono<? extends Throwable> handleBadRequest(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> {
                    LOG.error(BAD_REQUEST_ERROR + " -> {}", resp);
                    return new DiscordApiException(HttpStatus.BAD_REQUEST, resp.getError(),
                        resp.getErrorDescription(),
                        String.format(BAD_REQUEST_ERROR, resp.getError(), resp.getErrorDescription()));
                    });
    }

    private Mono<? extends Throwable> handleUnknownError(ClientResponse clientResponse) {

        return clientResponse.bodyToMono(DiscordErrorResponse.class)
                .map(resp -> {
                    LOG.error(UNKNOWN_ERROR + " -> {}", resp);
                    return new DiscordApiException(HttpStatus.INTERNAL_SERVER_ERROR, resp.getError(),
                            resp.getErrorDescription(),
                            String.format(UNKNOWN_ERROR, resp.getError(), resp.getErrorDescription()));
                });
    }
}
