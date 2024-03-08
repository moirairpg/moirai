package es.thalesalv.chatrpg.common.cqrs.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldHandler;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldResult;

@ExtendWith(MockitoExtension.class)
public class CommandRunnerImplTest {

    @Test
    public void errorWhenHandlerNotFound() {

        // Given
        CommandRunnerImpl runner = new CommandRunnerImpl();
        CreateWorld command = CreateWorld.builder().build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> runner.run(command));
    }

    @Test
    public void errorWhenHandlerAlreadyRegistered() {

        // Given
        CommandRunnerImpl runner = new CommandRunnerImpl();
        CreateWorldHandler handler = mock(CreateWorldHandler.class);

        runner.registerHandler(handler);

        // Then
        assertThrows(IllegalArgumentException.class,
                () -> runner.registerHandler(mock(CreateWorldHandler.class)));
    }

    @Test
    public void errorWhenHandlerIsNull() {

        // Given
        CommandRunnerImpl runner = new CommandRunnerImpl();
        CreateWorldHandler handler = null;

        // Then
        assertThrows(IllegalArgumentException.class,
                () -> runner.registerHandler(handler));
    }

    @Test
    public void registerAndRun() {

        // Given
        String id = "WLRDID";
        CommandRunnerImpl runner = new CommandRunnerImpl();
        CreateWorldHandler handler = mock(CreateWorldHandler.class);
        CreateWorld command = CreateWorld.builder().build();
        CreateWorldResult expectedResult = CreateWorldResult.build(id);

        when(handler.execute(any(CreateWorld.class))).thenReturn(expectedResult);

        runner.registerHandler(handler);

        // When
        CreateWorldResult result = runner.run(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
