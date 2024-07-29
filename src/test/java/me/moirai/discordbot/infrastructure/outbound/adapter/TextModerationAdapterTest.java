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
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ModerationResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ModerationResult;
import reactor.test.StepVerifier;

public class TextModerationAdapterTest extends AbstractWebMockTest {

    private TextModerationAdapter adapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void before() {

        objectMapper = new ObjectMapper();
        adapter = new TextModerationAdapter("http://localhost:" + PORT,
                "/moderation", "api-token", WebClient.builder());
    }

    @Test
    public void textModeration_whenValidRequest_thenOutputIsGenerated() throws JsonProcessingException {

        // Given
        String input = "This is the input";

        ModerationResponse expectedResponse = ModerationResponse.builder()
                .model("gpt-3.5")
                .id("id123")
                .results(Collections.singletonList(ModerationResult.builder()
                        .flagged(false)
                        .categoryScores(Collections.singletonMap("topic", "0.7"))
                        .categories(Collections.singletonMap("topic", true))
                        .build()))
                .build();

        wireMockServer.stubFor(any(anyUrl())
                .willReturn(aResponse().withBody(objectMapper.writeValueAsString(expectedResponse))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));

        // Then
        StepVerifier.create(adapter.moderate(input))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getModerationScores())
                            .containsAllEntriesOf(Collections.singletonMap("topic", 0.7));
                })
                .verifyComplete();
    }
}
