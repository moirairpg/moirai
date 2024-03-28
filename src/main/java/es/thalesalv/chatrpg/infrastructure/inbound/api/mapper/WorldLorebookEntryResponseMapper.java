package es.thalesalv.chatrpg.infrastructure.inbound.api.mapper;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryResult;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntriesResult;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateLorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.LorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchLorebookEntriesResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldLorebookEntryResponse;

@Component
public class WorldLorebookEntryResponseMapper {

    public SearchLorebookEntriesResponse toResponse(SearchWorldLorebookEntriesResult result) {

        List<LorebookEntryResponse> lorebook = CollectionUtils.emptyIfNull(result.getResults())
                .stream()
                .map(this::toResponse)
                .toList();

        return SearchLorebookEntriesResponse.builder()
                .page(result.getPage())
                .resultsInPage(result.getItems())
                .totalPages(result.getTotalPages())
                .totalResults(result.getTotalItems())
                .results(lorebook)
                .build();
    }

    public LorebookEntryResponse toResponse(GetWorldLorebookEntryResult result) {

        return LorebookEntryResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .regex(result.getRegex())
                .description(result.getDescription())
                .playerDiscordId(result.getPlayerDiscordId())
                .isPlayerCharacter(result.isPlayerCharacter())
                .creationDate(result.getCreationDate())
                .lastUpdateDate(result.getLastUpdateDate())
                .build();
    }

    public CreateLorebookEntryResponse toResponse(CreateWorldLorebookEntryResult result) {

        return CreateLorebookEntryResponse.build(result.getId());
    }

    public UpdateWorldLorebookEntryResponse toResponse(UpdateWorldLorebookEntryResult result) {

        return UpdateWorldLorebookEntryResponse.build(result.getLastUpdatedDateTime());
    }
}
