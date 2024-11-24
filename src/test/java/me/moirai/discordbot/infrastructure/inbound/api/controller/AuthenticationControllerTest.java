package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserDetailsResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.DiscordAuthResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponseFixture;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordAuthRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(properties = {
        "moirai.discord.oauth.client-id=clientId",
        "moirai.discord.oauth.client-secret=clientSecret",
        "moirai.discord.oauth.redirect-url=redirectUrl"
}, controllers = {
        AuthenticationController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class AuthenticationControllerTest extends AbstractRestWebTest {

    @MockBean
    protected DiscordAuthenticationPort discordAuthenticationPort;

    @MockBean
    private UserDataResponseMapper responseMapper;

    @Test
    public void exchangeCodeForToken() {

        // Given
        String code = "CODE";
        DiscordAuthResponse expectedResponse = DiscordAuthResponse.builder()
                .accessToken("TOKEN")
                .expiresIn(4324324L)
                .refreshToken("RFRSHTK")
                .scope("SCOPE")
                .build();

        when(discordAuthenticationPort.authenticate(any(DiscordAuthRequest.class)))
                .thenReturn(Mono.just(expectedResponse));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/auth/code")
                        .queryParam("code", code)
                        .build())
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(DiscordAuthResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getAccessToken()).isEqualTo(expectedResponse.getAccessToken());
                    assertThat(response.getRefreshToken()).isEqualTo(expectedResponse.getRefreshToken());
                    assertThat(response.getScope()).isEqualTo(expectedResponse.getScope());
                    assertThat(response.getExpiresIn()).isEqualTo(expectedResponse.getExpiresIn());
                });
    }

    @Test
    public void logout() {

        // Given
        when(discordAuthenticationPort.logout(any(DiscordTokenRevocationRequest.class)))
                .thenReturn(Mono.empty());

        // Then
        webTestClient.post()
                .uri("/auth/logout")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Void.class);
    }

    @Test
    public void http200WhenUserIsFound() {

        // Given
        UserDataResponse result = UserDataResponseFixture.create().build();

        when(useCaseRunner.run(any(GetUserDetailsById.class)))
                .thenReturn(mock(DiscordUserDetailsResult.class));

        when(responseMapper.toResponse(any(DiscordUserDetailsResult.class))).thenReturn(result);

        // Then
        webTestClient.get()
                .uri("/auth/user")
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(UserDataResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getId()).isEqualTo(result.getId());
                    assertThat(response.getEmail()).isEqualTo(result.getEmail());
                    assertThat(response.getGlobalNickname()).isEqualTo(result.getGlobalNickname());
                    assertThat(response.getUsername()).isEqualTo(result.getUsername());
                    assertThat(response.getAvatar()).isEqualTo(result.getAvatar());
                });

    }
}
