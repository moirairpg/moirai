package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.usecase.world.request.CreateWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.request.DeleteWorld;
import es.thalesalv.chatrpg.core.application.usecase.world.request.UpdateWorld;
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
                .usersAllowedToWrite(request.getUsersAllowedToWrite())
                .usersAllowedToRead(request.getUsersAllowedToRead())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public UpdateWorld toCommand(UpdateWorldRequest request, String worldId, String requesterDiscordId) {

        return UpdateWorld.builder()
                .id(worldId)
                .name(request.getName())
                .description(request.getDescription())
                .adventureStart(request.getAdventureStart())
                .visibility(request.getVisibility())
                .usersAllowedToWriteToAdd(request.getUsersAllowedToWriteToAdd())
                .usersAllowedToWriteToRemove(request.getUsersAllowedToWriteToRemove())
                .usersAllowedToReadToAdd(request.getUsersAllowedToReadToAdd())
                .usersAllowedToReadToRemove(request.getUsersAllowedToReadToRemove())
                .requesterDiscordId(requesterDiscordId)
                .build();
    }

    public DeleteWorld toCommand(String worldId, String requesterDiscordId) {

        return DeleteWorld.build(worldId, requesterDiscordId);
    }
}
