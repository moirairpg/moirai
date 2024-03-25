package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorldResult;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldResult;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateWorldResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchWorldsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.WorldResponse;

@Component
public class WorldResponseMapper {

    public SearchWorldsResponse toResponse(SearchWorldsResult result) {

        List<WorldResponse> worlds = result.getResults()
                .stream()
                .map(this::toResponse)
                .toList();

        return SearchWorldsResponse.builder()
                .page(result.getPage())
                .resultsInPage(result.getItems())
                .totalPages(result.getTotalPages())
                .results(worlds)
                .build();
    }

    public WorldResponse toResponse(GetWorldResult result) {

        return WorldResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .description(result.getDescription())
                .adventureStart(result.getAdventureStart())
                .visibility(result.getVisibility())
                .ownerDiscordId(result.getOwnerDiscordId())
                .creationDate(result.getCreationDate())
                .lastUpdateDate(result.getLastUpdateDate())
                .build();
    }

    public CreateWorldResponse toResponse(CreateWorldResult result) {

        return CreateWorldResponse.build(result.getId());
    }

    public UpdateWorldResponse toResponse(UpdateWorldResult result) {

        return UpdateWorldResponse.build(result.getLastUpdatedDateTime());
    }
}
