package es.thalesalv.chatrpg.core.application.command.world;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.world.WorldService;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldHandlerTest {

    @Mock
    private WorldService domainService;

    @InjectMocks
    private DeleteWorldHandler handler;

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String requesterDiscordId = "84REAC";
        String id = null;

        DeleteWorld config = DeleteWorld.build(id, requesterDiscordId);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteWorld() {

        // Given
        String requesterDiscordId = "84REAC";
        String id = "WRDID";

        DeleteWorld command = DeleteWorld.build(id, requesterDiscordId);

        doNothing().when(domainService).deleteWorld(any(DeleteWorld.class));

        // When
        handler.handle(command);
    }
}
