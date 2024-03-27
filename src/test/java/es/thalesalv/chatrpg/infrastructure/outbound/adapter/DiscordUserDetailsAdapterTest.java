package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.exception.DiscordApiException;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordErrorResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

public class DiscordUserDetailsAdapterTest {

    private static final String DUMMY_VALUE = "DUMMY";

    private static ObjectMapper objectMapper;
    private static DiscordUserDetailsAdapter adapter;
    private static MockWebServer mockBackEnd;

    @BeforeAll
    static void setUp() throws IOException {

        mockBackEnd = new MockWebServer();
        mockBackEnd.start();

        objectMapper = new ObjectMapper();
        adapter = new DiscordUserDetailsAdapter("http://localhost:" + mockBackEnd.getPort(), WebClient.builder());
    }

    @AfterAll
    static void tearDown() throws IOException {

        mockBackEnd.shutdown();
    }

    @Test
    public void getLoggedUser() throws JsonProcessingException {

        // Given
        String token = "TOKEN";

        DiscordUserDataResponse response = DiscordUserDataResponse.builder()
                .displayName("displayName")
                .username("username")
                .email("email@email.com")
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveLoggedUser(token))
                .assertNext(resp -> {
                    assertThat(resp).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void unauthorizedWhenGetLoggedUser() {

        // Given
        String token = "TOKEN";

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveLoggedUser(token))
                .expectError(DiscordApiException.class)
                .verify();
    }

    @Test
    public void badRequestWhenGetLoggedUser() throws JsonProcessingException {

        // Given
        String token = "TOKEN";

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveLoggedUser(token))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void internalErrorWhenGetLoggedUser() throws JsonProcessingException {

        // Given
        String token = "TOKEN";

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveLoggedUser(token))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void getUserById() throws JsonProcessingException {

        // Given
        String token = "TOKEN";
        String userId = "USRID";

        DiscordUserDataResponse response = DiscordUserDataResponse.builder()
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveUserById(token, userId))
                .assertNext(resp -> {
                    assertThat(resp).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void unauthorizedWhenGetUserById() {

        // Given
        String token = "TOKEN";
        String userId = "USRID";

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveUserById(token, userId))
                .expectError(DiscordApiException.class)
                .verify();
    }

    @Test
    public void badRequestWhenGetUserById() throws JsonProcessingException {

        // Given
        String token = "TOKEN";
        String userId = "USRID";

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveUserById(token, userId))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void internalErrorWhenGetUserById() throws JsonProcessingException {

        // Given
        String token = "TOKEN";
        String userId = "USRID";

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.retrieveUserById(token, userId))
                .verifyError(DiscordApiException.class);
    }
}
