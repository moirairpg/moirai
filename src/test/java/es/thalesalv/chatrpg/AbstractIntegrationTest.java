package es.thalesalv.chatrpg;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@ActiveProfiles("test")
@SpringBootTest(classes = ChatRpgApplication.class)
public abstract class AbstractIntegrationTest {

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
