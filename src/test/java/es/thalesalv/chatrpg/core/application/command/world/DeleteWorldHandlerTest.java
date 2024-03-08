package es.thalesalv.chatrpg.core.application.command.world;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldHandlerTest {

    @Mock
    private WorldRepository repository;

    @InjectMocks
    private DeleteWorldHandler handler;

    @Test
    public void errorWhenWorldIsNotFound() {

        // Given
        String id = "WRDID";

        DeleteWorld command = DeleteWorld.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.execute(command));
    }

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;

        DeleteWorld config = DeleteWorld.build(id);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.execute(config));
    }

    @Test
    public void deleteWorld() {

        // TODO make this an integration test that actually tests deletion

        // Given
        String id = "WRDID";

        DeleteWorld command = DeleteWorld.build(id);

        when(repository.findById(anyString()))
                .thenReturn(Optional.of(WorldFixture.privateWorld().build()));

        // When
        handler.execute(command);
    }
}
