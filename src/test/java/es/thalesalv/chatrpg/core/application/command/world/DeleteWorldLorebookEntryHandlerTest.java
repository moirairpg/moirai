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
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldLorebookEntryHandlerTest {

    @Mock
    private WorldLorebookEntryRepository repository;

    @InjectMocks
    private DeleteWorldLorebookEntryHandler handler;

    @Test
    public void errorWhenWorldIsNotFound() {

        // Given
        String id = "WRDID";

        DeleteWorldLorebookEntry command = DeleteWorldLorebookEntry.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenIdIsNull() {

        // Given
        String id = null;

        DeleteWorldLorebookEntry config = DeleteWorldLorebookEntry.build(id);

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(config));
    }

    @Test
    public void deleteWorld() {

        // Given
        String id = "WRDID";

        DeleteWorldLorebookEntry command = DeleteWorldLorebookEntry.build(id);

        when(repository.findById(anyString()))
                .thenReturn(Optional.of(WorldLorebookEntryFixture.sampleLorebookEntry().build()));

        // When
        handler.handle(command);
    }
}
