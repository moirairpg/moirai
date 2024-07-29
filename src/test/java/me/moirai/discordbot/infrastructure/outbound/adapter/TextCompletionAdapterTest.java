package me.moirai.discordbot.infrastructure.outbound.adapter;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.moirai.discordbot.AbstractWebMockTest;
import me.moirai.discordbot.core.application.model.request.TextGenerationRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessage;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.CompletionResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.CompletionResponseChoice;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.CompletionResponseUsage;
import reactor.test.StepVerifier;

public class TextCompletionAdapterTest extends AbstractWebMockTest {

    private TextCompletionAdapter adapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void before() {

        objectMapper = new ObjectMapper();
        adapter = new TextCompletionAdapter("http://localhost:" + PORT,
                "/completion", "api-token", WebClient.builder());
    }

    @Test
    public void textGeneration_whenValidRequest_thenOutputIsGenerated() throws JsonProcessingException {

        // Given
        TextGenerationRequest request = TextGenerationRequest.builder()
                .frequencyPenalty(1.0)
                .presencePenalty(1.0)
                .temperature(1.0)
                .maxTokens(100)
                .model("gpt-3.5")
                .logitBias(Collections.singletonMap("token", 1.0))
                .stopSequences(Collections.singletonList("token"))
                .build();

        CompletionResponse expectedResponse = CompletionResponse.builder()
                .usage(CompletionResponseUsage.builder()
                        .completionTokens(100)
                        .promptTokens(100)
                        .totalTokens(100)
                        .build())
                .choices(Collections.singletonList(CompletionResponseChoice.builder()
                        .message(ChatMessage.builder()
                                .content("Text output")
                                .build())
                        .build()))
                .build();

        wireMockServer.stubFor(any(anyUrl())
                .willReturn(aResponse().withBody(objectMapper.writeValueAsString(expectedResponse))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        // Then
        StepVerifier.create(adapter.generateTextFrom(request))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getCompletionTokens()).isEqualTo(100);
                    assertThat(result.getPromptTokens()).isEqualTo(100);
                    assertThat(result.getTotalTokens()).isEqualTo(100);
                    assertThat(result.getOutputText()).isEqualTo("Text output");
                })
                .verifyComplete();
    }
}
