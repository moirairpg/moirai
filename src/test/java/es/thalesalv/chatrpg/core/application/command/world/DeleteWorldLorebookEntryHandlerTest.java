package es.thalesalv.chatrpg.core.application.command.world;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.world.WorldService;

@ExtendWith(MockitoExtension.class)
public class DeleteWorldLorebookEntryHandlerTest {

    @Mock
    private WorldService domainService;

    @InjectMocks
    private DeleteWorldLorebookEntryHandler handler;

    @Test
    public void errorWhenEntryIdIsNull() {

        // Given
        DeleteWorldLorebookEntry command = DeleteWorldLorebookEntry.builder().build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenWorldIdIsNull() {

        // Given
        DeleteWorldLorebookEntry command = DeleteWorldLorebookEntry.builder()
                .lorebookEntryId("DUMMY")
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void deleteWorld() {

        // Given
        String id = "WRDID";
        String worldId = "WRLDID";
        String requesterId = "4234324";

        DeleteWorldLorebookEntry command = DeleteWorldLorebookEntry.builder()
                .lorebookEntryId(id)
                .worldId(worldId)
                .requesterDiscordId(requesterId)
                .build();

        doNothing().when(domainService).deleteLorebookEntry(any(DeleteWorldLorebookEntry.class));

        // When
        handler.handle(command);
    }
}
