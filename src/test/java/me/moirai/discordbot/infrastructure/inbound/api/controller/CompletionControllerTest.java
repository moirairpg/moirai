package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import me.moirai.discordbot.AbstractRestWebTest;
import me.moirai.discordbot.common.exception.AIModelNotSupportedException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.core.application.usecase.completion.request.CompleteText;
import me.moirai.discordbot.core.application.usecase.completion.result.CompleteTextResult;
import me.moirai.discordbot.infrastructure.inbound.api.request.TextCompletionRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.TextCompletionRequestFixture;
import me.moirai.discordbot.infrastructure.inbound.api.response.ErrorResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.TextCompletionResponse;
import me.moirai.discordbot.infrastructure.security.authentication.config.AuthenticationSecurityConfig;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = {
        CompletionController.class
}, excludeAutoConfiguration = {
        ReactiveSecurityAutoConfiguration.class,
        AuthenticationSecurityConfig.class
})
public class CompletionControllerTest extends AbstractRestWebTest {

    private static final String COMPLETIONS_BASE_URL = "/text-completion";

    @Test
    public void http200WhenTextIsGenerated() {

        // Given
        TextCompletionRequest request = TextCompletionRequestFixture.withStrictModeration();
        CompleteTextResult expectedResponse = CompleteTextResult.builder()
                .completionTokens(250)
                .outputText("Output")
                .promptTokens(250)
                .tokenIds(new long[] { 1 })
                .tokens(new String[] { "Output" })
                .totalTokens(1)
                .build();

        when(useCaseRunner.run(any(CompleteText.class))).thenReturn(Mono.just(expectedResponse));

        // Then
        webTestClient.post()
                .uri(COMPLETIONS_BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(TextCompletionResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCompletionTokens()).isEqualTo(expectedResponse.getCompletionTokens());
                    assertThat(response.getOutputText()).isEqualTo(expectedResponse.getOutputText());
                    assertThat(response.getPromptTokens()).isEqualTo(expectedResponse.getPromptTokens());
                    assertThat(response.getTokenIds()).isEqualTo(expectedResponse.getTokenIds());
                    assertThat(response.getTokens()).isEqualTo(expectedResponse.getTokens());
                    assertThat(response.getTotalTokens()).isEqualTo(expectedResponse.getTotalTokens());
                });
    }

    @Test
    public void http422WhenModerationTriggered() {

        // Given
        TextCompletionRequest request = TextCompletionRequestFixture.withStrictModeration();
        String moderationMessage = "Content blocked";
        List<String> topicsBlocked = list("violence", "self-harm");
        String[] expectedBlockedTopicsResponse = { "Topic flagged in content: violence",
                "Topic flagged in content: self-harm" };

        when(useCaseRunner.run(any(CompleteText.class)))
                .thenThrow(new ModerationException(moderationMessage, topicsBlocked));

        // Then
        webTestClient.post()
                .uri(COMPLETIONS_BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(response.getMessage()).isEqualTo(moderationMessage);
                    assertThat(response.getDetails()).isNotEmpty()
                            .containsExactlyInAnyOrder(expectedBlockedTopicsResponse);
                });
    }

    @Test
    public void http500WhenModelIsInvalid() {

        // Given
        TextCompletionRequest request = TextCompletionRequestFixture.withStrictModeration();
        String moderationMessage = "Invalid model";

        when(useCaseRunner.run(any(CompleteText.class)))
                .thenThrow(new AIModelNotSupportedException(moderationMessage));

        // Then
        webTestClient.post()
                .uri(COMPLETIONS_BASE_URL)
                .header(HttpHeaders.AUTHORIZATION, "TOKEN")
                .bodyValue(request)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(ErrorResponse.class)
                .value(response -> {
                    assertThat(response).isNotNull();
                    assertThat(response.getCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
                    assertThat(response.getDetails()).containsExactly(moderationMessage);
                });
    }
}
