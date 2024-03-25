package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorldRequestMapper {

    public CreateWorld toCommand(CreateWorldRequest request, String requesterDiscordId) {

        return CreateWorld.builder()
                .name(request.getName())
                .description(request.getDescription())
                .adventureStart(request.getAdventureStart())
                .visibility(request.getVisibility())
                .writerUsers(request.getWriterUsers())
                .readerUsers(request.getReaderUsers())
                .creatorDiscordId(requesterDiscordId)
                .build();
    }

    public UpdateWorld toCommand(UpdateWorldRequest request, String worldId, String requesterDiscordId) {

        return UpdateWorld.builder()
                .id(worldId)
                .name(request.getName())
                .description(request.getDescription())
                .adventureStart(request.getAdventureStart())
                .visibility(request.getVisibility())
                .writerUsersToAdd(request.getWriterUsersToAdd())
                .writerUsersToRemove(request.getWriterUsersToRemove())
                .readerUsersToAdd(request.getReaderUsersToAdd())
                .readerUsersToRemove(request.getReaderUsersToRemove())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public DeleteWorld toCommand(String worldId, String requesterDiscordId) {

        return DeleteWorld.build(worldId, requesterDiscordId);
    }
}
