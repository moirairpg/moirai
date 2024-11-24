package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.DiscordAuthResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordAuthRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import reactor.core.publisher.Mono;

@Hidden
@RestController
@RequestMapping("/auth")
public class AuthenticationController extends SecurityContextAware {

    private static final String TOKEN_TYPE_HINT = "access_token";
    private static final String DISCORD_SCOPE = "identify";
    private static final String DISCORD_GRANT_TYPE = "authorization_code";

    private final String clientId;
    private final String clientSecret;
    private final String redirectUrl;
    private final DiscordAuthenticationPort discordAuthenticationPort;
    private final UseCaseRunner useCaseRunner;
    private final UserDataResponseMapper responseMapper;

    public AuthenticationController(
            @Value("${moirai.discord.oauth.client-id}") String clientId,
            @Value("${moirai.discord.oauth.client-secret}") String clientSecret,
            @Value("${moirai.discord.oauth.redirect-url}") String redirectUrl,
            DiscordAuthenticationPort discordAuthenticationPort,
            UseCaseRunner useCaseRunner,
            UserDataResponseMapper responseMapper) {

        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
        this.discordAuthenticationPort = discordAuthenticationPort;
        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
    }

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

    @GetMapping("/user")
    public Mono<UserDataResponse> getAuthenticatedUserDetails() {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetUserDetailsById query = GetUserDetailsById.build(authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }
}
