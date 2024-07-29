package me.moirai.discordbot;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.github.tomakehurst.wiremock.WireMockServer;

public abstract class AbstractWebMockTest {

    protected static final String CONTENT_TYPE = "Content-Type";
    protected static final String APPLICATION_JSON = "application/json";
    protected static final int PORT = 1551;

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void setUp() throws IOException {

        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {

        wireMockServer.stop();
    }
}
