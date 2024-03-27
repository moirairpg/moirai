package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.common.exception.DiscordApiException;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordErrorResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import reactor.test.StepVerifier;

public class DiscordAuthenticationAdapterTest {

    private static final String DUMMY_VALUE = "DUMMY";

    private DiscordAuthenticationAdapter adapter;
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
        adapter = new DiscordAuthenticationAdapter("http://localhost:" + mockBackEnd.getPort(),
                WebClient.builder(), objectMapper);
    }

    @Test
    public void authenticateOnDiscord() throws JsonProcessingException {

        // Given
        DiscordAuthRequest request = DiscordAuthRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .code(DUMMY_VALUE)
                .grantType(DUMMY_VALUE)
                .redirectUri(DUMMY_VALUE)
                .scope(DUMMY_VALUE)
                .build();

        DiscordAuthResponse response = DiscordAuthResponse.builder()
                .accessToken(DUMMY_VALUE)
                .expiresIn(424234L)
                .refreshToken(DUMMY_VALUE)
                .scope(DUMMY_VALUE)
                .tokenType(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(response))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.authenticate(request))
                .assertNext(resp -> {
                    assertThat(resp).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    public void unauthorizedWhenAuthenticateOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordAuthRequest request = DiscordAuthRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .code(DUMMY_VALUE)
                .grantType(DUMMY_VALUE)
                .redirectUri(DUMMY_VALUE)
                .scope(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.authenticate(request))
                .expectError(DiscordApiException.class)
                .verify();
    }

    @Test
    public void badRequestWhenAuthenticateOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordAuthRequest request = DiscordAuthRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .code(DUMMY_VALUE)
                .grantType(DUMMY_VALUE)
                .redirectUri(DUMMY_VALUE)
                .scope(DUMMY_VALUE)
                .build();

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.authenticate(request))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void internalErrorWhenAuthenticateOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordAuthRequest request = DiscordAuthRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .code(DUMMY_VALUE)
                .grantType(DUMMY_VALUE)
                .redirectUri(DUMMY_VALUE)
                .scope(DUMMY_VALUE)
                .build();

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.authenticate(request))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void logoutOnDiscord() throws JsonProcessingException {

        // Given
        DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .token(DUMMY_VALUE)
                .tokenTypeHint(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.logout(request))
                .verifyComplete();
    }

    @Test
    public void unauthorizedWhenLogoutOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .token(DUMMY_VALUE)
                .tokenTypeHint(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.logout(request))
                .expectError(DiscordApiException.class)
                .verify();
    }

    @Test
    public void badRequestWhenLogoutOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .token(DUMMY_VALUE)
                .tokenTypeHint(DUMMY_VALUE)
                .build();

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.logout(request))
                .verifyError(DiscordApiException.class);
    }

    @Test
    public void internalErrorWhenLogoutOnDiscord()
            throws JsonProcessingException {

        // Given
        DiscordTokenRevocationRequest request = DiscordTokenRevocationRequest.builder()
                .clientId(DUMMY_VALUE)
                .clientSecret(DUMMY_VALUE)
                .token(DUMMY_VALUE)
                .tokenTypeHint(DUMMY_VALUE)
                .build();

        DiscordErrorResponse errorResponse = DiscordErrorResponse.builder()
                .errorDescription(DUMMY_VALUE)
                .error(DUMMY_VALUE)
                .build();

        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        // Then
        StepVerifier.create(adapter.logout(request))
                .verifyError(DiscordApiException.class);
    }
}
