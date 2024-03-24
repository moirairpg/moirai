package es.thalesalv.chatrpg.common.security.authentication;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.security.authentication.model.DiscordAuthRequest;
import es.thalesalv.chatrpg.common.security.authentication.model.DiscordAuthResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private static final String TOKEN_URI = "/oauth2/token";
    private static final String CONTENT_TYPE_VALUE = "application/x-www-form-urlencoded; charset=UTF-8";
    private static final String DISCORD_SCOPE = "identify";
    private static final String DISCORD_GRANT_TYPE = "authorization_code";

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    @Value("${chatrpg.discord.oauth.client-id}")
    private String clientId;

    @Value("${chatrpg.discord.oauth.client-secret}")
    private String clientSecret;

    @Value("${chatrpg.discord.oauth.redirect-url}")
    private String redirectUrl;

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public AuthenticationController(@Value("${chatrpg.discord.api-base-url}") final String discordBaseUrl,
            final WebClient.Builder webClientBuilder, final ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;
        this.webClient = webClientBuilder.baseUrl(discordBaseUrl)
                .build();
    }

    @GetMapping("/code")
    public Mono<DiscordAuthResponse> codeExchange(@RequestParam("code") String code) {

        final DiscordAuthRequest request = DiscordAuthRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUrl)
                .scope(DISCORD_SCOPE)
                .grantType(DISCORD_GRANT_TYPE)
                .build();

        return authenticate(request);
    }

    public Mono<DiscordAuthResponse> authenticate(final DiscordAuthRequest request) {

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
                .bodyToMono(DiscordAuthResponse.class);
    }
}
