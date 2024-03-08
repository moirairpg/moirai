package es.thalesalv.chatrpg.core.application.command.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.core.domain.world.WorldDomainServiceImpl;
import es.thalesalv.chatrpg.core.domain.world.WorldFixture;

@ExtendWith(MockitoExtension.class)
public class UpdateWorldHandlerTest {

    @Mock
    private WorldDomainServiceImpl service;

    @InjectMocks
    private UpdateWorldHandler handler;

    @Test
    public void updateWorld() {

        // Given
        String id = "WRDID";

        UpdateWorld command = UpdateWorld.builder()
                .id(id)
                .name("ChatRPG")
                .description("This is an RPG world")
                .adventureStart("As you enter the city, people around you start looking at you.")
                .visibility("PUBLIC")
                .creatorDiscordId("CRTID")
                .build();

        World expectedUpdatedWorld = WorldFixture.privateWorld().build();

        when(service.update(any(UpdateWorld.class)))
                .thenReturn(expectedUpdatedWorld);

        // When
        UpdateWorldResult result = handler.handle(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getLastUpdatedDateTime()).isEqualTo(expectedUpdatedWorld.getLastUpdateDate());
    }
}
