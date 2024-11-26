package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserDetailsResult;
import me.moirai.discordbot.core.application.usecase.discord.userdetails.GetUserDetailsById;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.UserDataResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponseFixture;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;

@WebFluxTest(controllers = {
        UserDetailsController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class UserDetailsControllerTest extends AbstractRestWebTest {

    private static final String USER_ID_BASE_URL = "/user/%s";

    @MockBean
    private UserDataResponseMapper responseMapper;

    @Test
    public void http200WhenUserIsFound() {

        // Given
        String userId = "1234";
        UserDataResponse result = UserDataResponseFixture.create()
                .id(userId)
                .build();

        when(useCaseRunner.run(any(GetUserDetailsById.class)))
                .thenReturn(mock(DiscordUserDetailsResult.class));

        when(responseMapper.toResponse(any(DiscordUserDetailsResult.class))).thenReturn(result);

        // Then
        webTestClient.get()
                .uri(String.format(USER_ID_BASE_URL, userId))
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
