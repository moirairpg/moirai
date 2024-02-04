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

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainService;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

@ExtendWith(MockitoExtension.class)
public class CreateWorldHandlerTest {

    @Mock
    private WorldDomainService domainService;

    @InjectMocks
    private CreateWorldHandler handler;

    @Test
    public void errorWhenCommandIsNull() {

        // Given
        CreateWorld command = null;

        // Then
        assertThrows(IllegalArgumentException.class, () -> handler.execute(command));
    }

    @Test
    public void createWorld() {

        // Given
        String id = "HAUDHUAHD";
        World world = WorldFixture.privateWorld().id(id).build();
        CreateWorld command = CreateWorldFixture.createPrivateWorld().build();

        when(domainService.createFrom(any(CreateWorld.class)))
                .thenReturn(world);

        // When
        CreateWorldResult result = handler.execute(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(id);
    }
}
