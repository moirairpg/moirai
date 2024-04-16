package es.thalesalv.chatrpg.core.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.common.usecases.UseCaseRunnerImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class UseCaseConfig {

    private static final String REGISTERED_COMMAND_HANDLERS = "{} use case handlers have been registered";

    @Bean
    public UseCaseRunner seCaseRunner(List<UseCaseHandler<?, ?>> handlers) {

        UseCaseRunner runner = new UseCaseRunnerImpl();
        handlers.forEach(runner::registerHandler);

        log.info(REGISTERED_COMMAND_HANDLERS, handlers.size());

        return runner;
    }
}
