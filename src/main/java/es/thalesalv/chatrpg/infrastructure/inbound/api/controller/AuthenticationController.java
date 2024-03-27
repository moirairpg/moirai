package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.common.web.SecurityContextAware;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Hidden
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController extends SecurityContextAware {

    private static final String TOKEN_TYPE_HINT = "access_token";
    private static final String DISCORD_SCOPE = "identify";
    private static final String DISCORD_GRANT_TYPE = "authorization_code";

    @Value("${chatrpg.discord.oauth.client-id}")
    private String clientId;

    @Value("${chatrpg.discord.oauth.client-secret}")
    private String clientSecret;

    @Value("${chatrpg.discord.oauth.redirect-url}")
    private String redirectUrl;

    private final DiscordAuthenticationPort discordAuthenticationPort;

    @GetMapping("/code")
    public Mono<DiscordAuthResponse> codeExchange(@RequestParam("code") String code) {

        DiscordAuthRequest request = DiscordAuthRequest.builder()
                .code(code)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUrl)
                .scope(DISCORD_SCOPE)
                .grantType(DISCORD_GRANT_TYPE)
                .build();

        return discordAuthenticationPort.authenticate(request);
    }

    @PostMapping("/logout")
    public Mono<Void> logout() {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {
            DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .token(authenticatedUser.getAuthorizationToken())
                    .tokenTypeHint(TOKEN_TYPE_HINT)
                    .build();

            return discordAuthenticationPort.logout(request);
        });
    }
}
