package me.moirai.discordbot;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

public abstract class AbstractWebMockTest {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final int PORT = 1551;

    protected static WireMockServer wireMockServer;
    protected static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() throws IOException {

        objectMapper = new ObjectMapper();

        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {

        wireMockServer.stop();
    }

    protected void prepareWebserverFor(Object response, int httpStatus) throws JsonProcessingException {

        wireMockServer.stubFor(any(anyUrl())
                .willReturn(aResponse()
                        .withStatus(httpStatus)
                        .withBody(objectMapper.writeValueAsString(response))
                        .withHeader(CONTENT_TYPE, APPLICATION_JSON)));
    }

    protected void prepareWebserverFor(int httpStatus) {

        wireMockServer.stubFor(any(anyUrl())
                .willReturn(aResponse().withStatus(httpStatus)));
    }
}
