package me.moirai.discordbot.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;

import me.moirai.discordbot.AbstractWebMockTest;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ModerationResponse;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ModerationResult;
import reactor.test.StepVerifier;

public class TextModerationAdapterTest extends AbstractWebMockTest {

    private TextModerationAdapter adapter;

    @BeforeEach
    void before() {

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

        prepareWebserverFor(expectedResponse, 200);

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
