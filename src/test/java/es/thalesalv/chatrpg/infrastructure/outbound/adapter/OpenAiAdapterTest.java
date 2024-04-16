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

import es.thalesalv.chatrpg.core.application.model.request.TextGenerationRequest;
import es.thalesalv.chatrpg.core.application.model.request.TextModerationRequest;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessage;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.CompletionResponse;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.CompletionResponseChoice;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.CompletionResponseUsage;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResponse;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ModerationResult;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

public class OpenAiAdapterTest {

    private OpenAiAdapter adapter;
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
        adapter = new OpenAiAdapter("http://localhost:" + mockBackEnd.getPort(),
                "/moderation", "/completion", "api-token",
                WebClient.builder());
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

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(expectedResponse))
                .addHeader("Content-Type", "application/json"));

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

    @Test
    public void textModeration_whenValidRequest_thenOutputIsGenerated() throws JsonProcessingException {

        // Given
        TextModerationRequest request = TextModerationRequest.build("This is the input");

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
        StepVerifier.create(adapter.moderateTextFrom(request))
                .assertNext(result -> {
                    assertThat(result).isNotNull();
                    assertThat(result.getModerationScores())
                            .containsAllEntriesOf(Collections.singletonMap("topic", 0.7));
                })
                .verifyComplete();
    }
}
