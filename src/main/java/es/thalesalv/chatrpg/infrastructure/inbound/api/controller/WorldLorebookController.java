package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.common.web.SecurityContextAware;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorldLorebookEntry;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldLorebookEntryById;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldLorebookEntryRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldLorebookEntryResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldLorebookEntryRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.SearchParameters;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldLorebookEntryRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateLorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.LorebookEntryResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchLorebookEntriesResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldLorebookEntryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/world/{worldId}/lorebook")
@RequiredArgsConstructor
@Tag(name = "World Lorebooks", description = "Endpoints for managing ChatRPG World Lorebooks")
public class WorldLorebookController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final WorldLorebookEntryRequestMapper requestMapper;
    private final WorldLorebookEntryResponseMapper responseMapper;

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchLorebookEntriesResponse> searchLorebook(
            @PathVariable(name = "worldId", required = true) String worldId,
            SearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchWorldLorebookEntries query = SearchWorldLorebookEntries.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .worldId(worldId)
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<LorebookEntryResponse> getLorebookEntryById(
            @PathVariable(name = "worldId", required = true) String worldId,
            @PathVariable(name = "entryId", required = true) String entryId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetWorldLorebookEntryById query = GetWorldLorebookEntryById.builder()
                    .entryId(entryId)
                    .worldId(worldId)
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreateLorebookEntryResponse> createLorebookEntry(
            @PathVariable(name = "worldId", required = true) String worldId,
            @Valid @RequestBody CreateWorldLorebookEntryRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            CreateWorldLorebookEntry command = requestMapper.toCommand(request, worldId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @PutMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdateWorldLorebookEntryResponse> updateLorebookEntry(
            @PathVariable(name = "worldId", required = true) String worldId,
            @PathVariable(name = "entryId", required = true) String entryId,
            @Valid @RequestBody UpdateWorldLorebookEntryRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            UpdateWorldLorebookEntry command = requestMapper.toCommand(request, entryId,
                    worldId, authenticatedUser.getId());

            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @DeleteMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deleteLorebookEntry(
            @PathVariable(name = "worldId", required = true) String worldId,
            @PathVariable(name = "entryId", required = true) String entryId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeleteWorldLorebookEntry command = requestMapper.toCommand(entryId, worldId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }
}
