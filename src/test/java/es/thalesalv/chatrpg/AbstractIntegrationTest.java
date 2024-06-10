package es.thalesalv.chatrpg;

import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import discord4j.core.GatewayDiscordClient;
import es.thalesalv.chatrpg.core.application.port.DiscordAuthenticationPort;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import es.thalesalv.chatrpg.core.application.port.TextCompletionPort;
import es.thalesalv.chatrpg.core.application.port.TextModerationPort;
import es.thalesalv.chatrpg.core.application.service.PersonaEnrichmentService;
import es.thalesalv.chatrpg.core.application.service.StorySummarizationService;
import es.thalesalv.chatrpg.infrastructure.config.Discord4JConfig;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatRpgApplication.class)
public abstract class AbstractIntegrationTest {

    @Mock
    private DiscordAuthenticationPort discordAuthenticationPort;

    @MockBean
    private TextCompletionPort openAiPort;

    @MockBean
    private DiscordChannelPort discordChannelOperationsPort;

    @MockBean
    private PersonaEnrichmentService inputEnrichmentService;

    @MockBean
    private StorySummarizationService contextSummarizationService;

    @MockBean
    private GatewayDiscordClient gatewayDiscordClient;

    @MockBean
    private TextModerationPort textModerationPort;

    @MockBean
    private Discord4JConfig discord4jConfig;

    private static final String POSTGRES_IMAGE_NAME = "postgres:15-alpine";

    static PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE_NAME)
            .withDatabaseName("chatrpg")
            .withUsername("chatrpg")
            .withPassword("chatrpg");

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {

        container.start();

        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }
}
