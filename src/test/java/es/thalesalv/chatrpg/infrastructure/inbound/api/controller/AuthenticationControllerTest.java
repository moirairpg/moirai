package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;

import es.thalesalv.chatrpg.AbstractRestWebTest;
import es.thalesalv.chatrpg.core.application.model.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.core.application.model.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import es.thalesalv.chatrpg.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(properties = {
        "chatrpg.discord.oauth.client-id=clientId",
        "chatrpg.discord.oauth.client-secret=clientSecret",
        "chatrpg.discord.oauth.redirect-url=redirectUrl"
}, controllers = {
        AuthenticationController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class AuthenticationControllerTest extends AbstractRestWebTest {

    @MockBean
    protected DiscordAuthenticationPort discordAuthenticationPort;

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
}
