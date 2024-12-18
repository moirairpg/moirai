package me.moirai.discordbot.infrastructure.inbound.api.controller;

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

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorldLorebookEntry;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldLorebookEntryById;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorldLorebookEntry;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldLorebookEntryRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldLorebookEntryResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.SearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateLorebookEntryRequest;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateLorebookEntryResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.LorebookEntryResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchLorebookEntriesResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateLorebookEntryResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/world/{worldId}/lorebook")
@Tag(name = "World Lorebooks", description = "Endpoints for managing MoirAI World Lorebooks")
public class WorldLorebookController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final WorldLorebookEntryRequestMapper requestMapper;
    private final WorldLorebookEntryResponseMapper responseMapper;

    public WorldLorebookController(UseCaseRunner useCaseRunner,
            WorldLorebookEntryRequestMapper requestMapper,
            WorldLorebookEntryResponseMapper responseMapper) {

        this.useCaseRunner = useCaseRunner;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

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
            @Valid @RequestBody CreateLorebookEntryRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            CreateWorldLorebookEntry command = requestMapper.toCommand(request, worldId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @PutMapping("/{entryId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdateLorebookEntryResponse> updateLorebookEntry(
            @PathVariable(name = "worldId", required = true) String worldId,
            @PathVariable(name = "entryId", required = true) String entryId,
            @Valid @RequestBody UpdateLorebookEntryRequest request) {

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
