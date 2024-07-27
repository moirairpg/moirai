package me.moirai.discordbot;

import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import discord4j.core.GatewayDiscordClient;
import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.PersonaEnrichmentPort;
import me.moirai.discordbot.core.application.port.StorySummarizationPort;
import me.moirai.discordbot.core.application.port.TextCompletionPort;
import me.moirai.discordbot.core.application.port.TextModerationPort;
import me.moirai.discordbot.infrastructure.config.Discord4JConfig;

@ActiveProfiles("test")
@SpringBootTest(classes = MoiraiApplication.class)
public abstract class AbstractIntegrationTest {

    @Mock
    private DiscordAuthenticationPort discordAuthenticationPort;

    @MockBean
    private TextCompletionPort openAiPort;

    @MockBean
    private DiscordChannelPort discordChannelOperationsPort;

    @MockBean
    private PersonaEnrichmentPort inputEnrichmentService;

    @MockBean
    private StorySummarizationPort contextSummarizationService;

    @MockBean
    private GatewayDiscordClient gatewayDiscordClient;

    @MockBean
    private TextModerationPort textModerationPort;

    @MockBean
    private Discord4JConfig discord4jConfig;

    private static final String POSTGRES_IMAGE_NAME = "postgres:15-alpine";

    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)
            .withDatabaseName("moirai")
            .withUsername("moirai")
            .withPassword("moirai");

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {

        container.start();

        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
