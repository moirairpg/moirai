package es.thalesalv.chatrpg.core.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import es.thalesalv.chatrpg.common.cqrs.command.CommandHandler;
import es.thalesalv.chatrpg.common.cqrs.command.CommandRunner;
import es.thalesalv.chatrpg.common.cqrs.command.CommandRunnerImpl;
import es.thalesalv.chatrpg.common.cqrs.query.QueryHandler;
import es.thalesalv.chatrpg.common.cqrs.query.QueryRunner;
import es.thalesalv.chatrpg.common.cqrs.query.QueryRunnerImpl;

@Configuration
public class CqrsConfig {

    private static final String REGISTERED_COMMAND_HANDLERS = "{} command handlers have been registered";
    private static final String REGISTERED_QUERY_HANDLERS = "{} query handlers have been registered";

    private static final Logger LOGGER = LoggerFactory.getLogger(CqrsConfig.class);

    @Bean
    public CommandRunner commandRunner(List<CommandHandler<?, ?>> handlers) {

        CommandRunner runner = new CommandRunnerImpl();
        handlers.forEach(runner::registerHandler);

        LOGGER.info(REGISTERED_COMMAND_HANDLERS, handlers.size());

        return runner;
    }

    @Bean
    public QueryRunner queryRunner(List<QueryHandler<?, ?>> handlers) {

        QueryRunner runner = new QueryRunnerImpl();
        handlers.forEach(runner::registerHandler);

        LOGGER.info(REGISTERED_QUERY_HANDLERS, handlers.size());

        return runner;
    }
}
