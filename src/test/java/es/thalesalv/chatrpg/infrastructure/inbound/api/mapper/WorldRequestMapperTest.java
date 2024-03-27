package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldRequestFixture;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldRequestFixture;

@ExtendWith(MockitoExtension.class)
public class WorldRequestMapperTest {

    @InjectMocks
    private WorldRequestMapper mapper;

    @Test
    public void creationRequestToCommand() {

        // Given
        String requesterId = "RQSTRID";
        CreateWorldRequest request = CreateWorldRequestFixture.createPrivateWorld().build();

        // When
        CreateWorld command = mapper.toCommand(request, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getDescription()).isEqualTo(request.getDescription());
        assertThat(command.getAdventureStart()).isEqualTo(request.getAdventureStart());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getCreatorDiscordId()).isEqualTo(requesterId);
        assertThat(command.getWriterUsers()).hasSameElementsAs(request.getWriterUsers());
        assertThat(command.getReaderUsers()).hasSameElementsAs(request.getReaderUsers());
    }

    @Test
    public void updateRequestToCommand() {

        // Given
        String worldId = "WRLDID";
        String requesterId = "RQSTRID";
        UpdateWorldRequest request = UpdateWorldRequestFixture.createPrivateWorld().build();

        // When
        UpdateWorld command = mapper.toCommand(request, worldId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(request.getName());
        assertThat(command.getDescription()).isEqualTo(request.getDescription());
        assertThat(command.getAdventureStart()).isEqualTo(request.getAdventureStart());
        assertThat(command.getVisibility()).isEqualTo(request.getVisibility());
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
        assertThat(command.getWriterUsersToAdd()).hasSameElementsAs(request.getWriterUsersToAdd());
        assertThat(command.getWriterUsersToRemove()).hasSameElementsAs(request.getWriterUsersToRemove());
        assertThat(command.getReaderUsersToAdd()).hasSameElementsAs(request.getReaderUsersToAdd());
        assertThat(command.getReaderUsersToRemove()).hasSameElementsAs(request.getReaderUsersToRemove());
    }

    @Test
    public void deleteRequestToCommand() {

        // Given
        String worldId = "WRLDID";
        String requesterId = "RQSTRID";

        // When
        DeleteWorld command = mapper.toCommand(worldId, requesterId);

        // Then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(worldId);
        assertThat(command.getRequesterDiscordId()).isEqualTo(requesterId);
    }
}
