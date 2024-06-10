package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResponse;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

public class TextModerationAdapterTest {

    private TextModerationAdapter adapter;
    private ObjectMapper objectMapper;

    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    void before() {

        objectMapper = new ObjectMapper();
        adapter = new TextModerationAdapter("http://localhost:" + mockBackEnd.getPort(),
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

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expectedResponse))
                .addHeader("Content-Type", "application/json"));

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
