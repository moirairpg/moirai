package es.thalesalv.chatrpg.infrastructure.outbound.persistence.mapper;

import static org.apache.commons.lang3.StringUtils.isBlank;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.usecase.world.result.GetWorldResult;
import es.thalesalv.chatrpg.core.application.usecase.world.result.SearchWorldsResult;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import es.thalesalv.chatrpg.core.domain.world.World;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.world.WorldEntity;

@Component
public class WorldPersistenceMapper {

    public WorldEntity mapToEntity(World world) {

        String creatorOrOwnerDiscordId = isBlank(world.getCreatorDiscordId())
                ? world.getOwnerDiscordId()
                : world.getCreatorDiscordId();

        return WorldEntity.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(world.getVisibility().toString())
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .creationDate(world.getCreationDate())
                .creatorDiscordId(creatorOrOwnerDiscordId)
                .lastUpdateDate(world.getLastUpdateDate())
                .build();
    }

    public World mapFromEntity(WorldEntity world) {

        Permissions permissions = Permissions.builder()
                .ownerDiscordId(world.getOwnerDiscordId())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .build();

        return World.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .visibility(Visibility.fromString(world.getVisibility()))
                .permissions(permissions)
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .creatorDiscordId(world.getCreatorDiscordId())
                .build();
    }

    public GetWorldResult mapToResult(WorldEntity world) {

        return GetWorldResult.builder()
                .id(world.getId())
                .name(world.getName())
                .description(world.getDescription())
                .adventureStart(world.getAdventureStart())
                .usersAllowedToRead(world.getUsersAllowedToRead())
                .usersAllowedToWrite(world.getUsersAllowedToWrite())
                .visibility(world.getVisibility())
                .ownerDiscordId(world.getOwnerDiscordId())
                .creationDate(world.getCreationDate())
                .lastUpdateDate(world.getLastUpdateDate())
                .build();
    }

    public SearchWorldsResult mapToResult(Page<WorldEntity> pagedResult) {

        return SearchWorldsResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(pagedResult.getNumber() + 1)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }
}
