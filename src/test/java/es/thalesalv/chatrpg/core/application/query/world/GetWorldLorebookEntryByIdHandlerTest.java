package es.thalesalv.chatrpg.core.application.query.world;

import static org.assertj.core.api.Assertions.assertThat;
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
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntry;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldLorebookEntryRepository;

@ExtendWith(MockitoExtension.class)
public class GetWorldLorebookEntryByIdHandlerTest {
    @Mock
    private WorldLorebookEntryRepository repository;

    @InjectMocks
    private GetWorldLorebookEntryByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetWorldLorebookEntryById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getWorldLorebookEntryById() {

        // Given
        String id = "HAUDHUAHD";
        WorldLorebookEntry world = WorldLorebookEntryFixture.sampleLorebookEntry().id(id).build();
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.of(world));

        // When
        GetWorldLorebookEntryResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void errorWhenWorldLorebookEntryNotFound() {

        // Given
        String id = "HAUDHUAHD";
        GetWorldLorebookEntryById query = GetWorldLorebookEntryById.build(id);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }
}
