package es.thalesalv.chatrpg.infrastructure.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class LiquibaseConfig {

    private final DataSource dataSource;

    @Value("${spring.liquibase.enabled}")
    private boolean enabled;

    @Value("${spring.liquibase.defaultSchema}")
    private String defaultSchema;

    @Value("${spring.liquibase.change-log}")
    private String changeLogPath;

    @Bean
    public SpringLiquibase liquibase() {

        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogPath);
        liquibase.setDefaultSchema(defaultSchema);

        return liquibase;
    }
}
