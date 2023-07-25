package es.thalesalv.chatrpg.adapters.rest.client;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.domain.model.discord.DiscordAuthRequest;
import es.thalesalv.chatrpg.domain.model.discord.DiscordAuthResponse;
import es.thalesalv.chatrpg.domain.model.discord.DiscordUserData;
import reactor.core.publisher.Mono;

@Service
public class DiscordApiService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    private static final String BEARER = "Bearer ";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordApiService.class);

    public DiscordApiService(@Value("${chatrpg.discord.api-base-url}") final String openAiBaseUrl,
            final WebClient.Builder webClientBuilder, final ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(openAiBaseUrl)
                .build();
    }

    public Mono<DiscordUserData> retrieveLoggedUser(final String authorization) {

        return webClient.get()
                .uri("/users/@me")
                .headers(headers -> {
                    headers.add(HttpHeaders.AUTHORIZATION, BEARER + authorization);
                    headers.add(HttpHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON_VALUE);
                })
                .retrieve()
                .bodyToMono(DiscordUserData.class);
    }

    public Mono<DiscordAuthResponse> authenticate(final DiscordAuthRequest request) {

        LOGGER.debug("Authenticating user on Discord");
        final MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        Map<String, String> fieldMap = objectMapper.convertValue(request, new TypeReference<Map<String, String>>() {
        });

        valueMap.setAll(fieldMap);
        return webClient.post()
                .uri("/oauth2/token")
                .headers(headers -> {
                    headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                    headers.add("Accept", "application/x-www-form-urlencoded; charset=UTF-8");
                })
                .body(BodyInserters.fromFormData(valueMap))
                .retrieve()
                .bodyToMono(DiscordAuthResponse.class);
    }
}
