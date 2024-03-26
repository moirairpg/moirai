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

import es.thalesalv.chatrpg.common.exception.AssetAccessDeniedException;
import es.thalesalv.chatrpg.common.exception.AssetNotFoundException;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;
import es.thalesalv.chatrpg.core.domain.world.WorldRepository;

@ExtendWith(MockitoExtension.class)
public class GetWorldByIdHandlerTest {

    @Mock
    private WorldRepository repository;

    @InjectMocks
    private GetWorldByIdHandler handler;

    @Test
    public void errorWhenQueryIsNull() {

        // Given
        GetWorldById query = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(query));
    }

    @Test
    public void getWorldById() {

        // Given
        String requesterDiscordId = "586678721356875";
        String id = "HAUDHUAHD";
        World world = WorldFixture.privateWorld().id(id).build();
        GetWorldById query = GetWorldById.build(id, requesterDiscordId);

        when(repository.findById(anyString())).thenReturn(Optional.of(world));

        // When
        GetWorldResult result = handler.handle(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    public void errorWhenWorldNotFound() {

        // Given
        String requesterDiscordId = "586678721356875";
        String id = "HAUDHUAHD";
        GetWorldById query = GetWorldById.build(id, requesterDiscordId);

        when(repository.findById(anyString())).thenReturn(Optional.empty());

        // Then
        assertThrows(AssetNotFoundException.class, () -> handler.handle(query));
    }

    @Test
    public void errorWhenUnauthorizedUserGetWorld() {

        // Given
        String requesterDiscordId = "INVLDUSR";
        String id = "HAUDHUAHD";
        World world = WorldFixture.privateWorld().id(id).build();
        GetWorldById query = GetWorldById.build(id, requesterDiscordId);

        when(repository.findById(anyString())).thenReturn(Optional.of(world));

        // Then
        assertThrows(AssetAccessDeniedException.class, () -> handler.handle(query));
    }
}
