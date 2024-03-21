package es.thalesalv.chatrpg.core.application.command.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;

@ExtendWith(MockitoExtension.class)
public class CreateWorldLorebookEntryHandlerTest {

    @Mock
    private WorldDomainService domainService;

    @InjectMocks
    private CreateWorldLorebookEntryHandler handler;

    @Test
    public void errorWhenCommandIsNull() {

        // Given
        CreateWorldLorebookEntry command = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenWorldIdIsNull() {

        // Given
        CreateWorldLorebookEntry command = CreateWorldLorebookEntryFixture.sampleLorebookEntry()
                .worldId(null)
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenNameIsNull() {

        // Given
        CreateWorldLorebookEntry command = CreateWorldLorebookEntryFixture.sampleLorebookEntry()
                .name(null)
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void errorWhenDescriptionIsNull() {

        // Given
        CreateWorldLorebookEntry command = CreateWorldLorebookEntryFixture.sampleLorebookEntry()
                .description(null)
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void createWorldLorebookEntry() {

        // Given
        String id = "HAUDHUAHD";
        WorldLorebookEntry entry = WorldLorebookEntryFixture.sampleLorebookEntry().id(id).build();
        CreateWorldLorebookEntry command = CreateWorldLorebookEntryFixture.sampleLorebookEntry().build();

        when(domainService.createLorebookEntry(any(CreateWorldLorebookEntry.class)))
                .thenReturn(entry);

        // When
        CreateWorldLorebookEntryResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
