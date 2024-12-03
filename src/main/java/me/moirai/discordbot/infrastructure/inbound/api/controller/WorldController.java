package me.moirai.discordbot.infrastructure.inbound.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.world.request.AddFavoriteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.CreateWorld;
import me.moirai.discordbot.core.application.usecase.world.request.DeleteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.GetWorldById;
import me.moirai.discordbot.core.application.usecase.world.request.RemoveFavoriteWorld;
import me.moirai.discordbot.core.application.usecase.world.request.SearchFavoriteWorlds;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.world.request.UpdateWorld;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.WorldResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateWorldRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateWorldRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.WorldSearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateWorldResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchWorldsResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateWorldResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.WorldResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/world")
@Tag(name = "Worlds", description = "Endpoints for managing MoirAI Worlds")
public class WorldController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final WorldResponseMapper responseMapper;
    private final WorldRequestMapper requestMapper;

    public WorldController(UseCaseRunner useCaseRunner,
            WorldResponseMapper responseMapper,
            WorldRequestMapper requestMapper) {

        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
        this.requestMapper = requestMapper;
    }

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchWorldsResponse> searchWorldsWithReadAccess(WorldSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .visibility(searchParameters.getVisibility())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/search/own")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchWorldsResponse> searchWorldsWithWriteAccess(WorldSearchParameters searchParameters,
            Authentication authentication) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .visibility(searchParameters.getVisibility())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/search/favorites")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchWorldsResponse> searchFavoritesWorlds(WorldSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchFavoriteWorlds query = SearchFavoriteWorlds.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .visibility(searchParameters.getVisibility())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{worldId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<WorldResponse> getWorldById(@PathVariable(name = "worldId", required = true) String worldId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetWorldById query = GetWorldById.build(worldId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreateWorldResponse> createWorld(@Valid @RequestBody CreateWorldRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            CreateWorld command = requestMapper.toCommand(request, authenticatedUser.getId());
            return useCaseRunner.run(command);
        }).map(responseMapper::toResponse);
    }

    @PutMapping("/{worldId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdateWorldResponse> updateWorld(@PathVariable(name = "worldId", required = true) String worldId,
            @Valid @RequestBody UpdateWorldRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            UpdateWorld command = requestMapper.toCommand(request, worldId, authenticatedUser.getId());
            return useCaseRunner.run(command);
        }).map(responseMapper::toResponse);
    }

    @DeleteMapping("/{worldId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deleteWorld(@PathVariable(name = "worldId", required = true) String worldId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeleteWorld command = requestMapper.toCommand(worldId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @PostMapping("/favorite")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> addFavoriteWorld(@RequestBody FavoriteRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            AddFavoriteWorld command = AddFavoriteWorld.builder()
                    .assetId(request.getAssetId())
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @DeleteMapping("/favorite/{assetId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> removeFavoriteChannelConfig(@PathVariable(required = true) String assetId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            RemoveFavoriteWorld command = RemoveFavoriteWorld.builder()
                    .assetId(assetId)
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }
}
