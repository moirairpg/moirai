package me.moirai.discordbot.infrastructure.inbound.api.controller;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.text.CaseUtils.toCamelCase;

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

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.common.web.SecurityContextAware;
import me.moirai.discordbot.core.application.usecase.adventure.request.AddFavoriteAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.CreateAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.DeleteAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureById;
import me.moirai.discordbot.core.application.usecase.adventure.request.RemoveFavoriteAdventure;
import me.moirai.discordbot.core.application.usecase.adventure.request.SearchAdventures;
import me.moirai.discordbot.core.application.usecase.adventure.request.UpdateAdventure;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.AdventureRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.AdventureResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.AdventureSearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateAdventureRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateAdventureRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchDirection;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchGameMode;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchModel;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchModeration;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchOperation;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchSortingField;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchVisibility;
import me.moirai.discordbot.infrastructure.inbound.api.response.AdventureResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateAdventureResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchAdventuresResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateAdventureResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/adventure")
@Tag(name = "Adventures", description = "Endpoints for managing MoirAI Adventures")
public class AdventureController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final AdventureResponseMapper responseMapper;
    private final AdventureRequestMapper requestMapper;

    public AdventureController(UseCaseRunner useCaseRunner,
            AdventureResponseMapper responseMapper,
            AdventureRequestMapper requestMapper) {

        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
        this.requestMapper = requestMapper;
    }

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchAdventuresResponse> search(AdventureSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchAdventures query = SearchAdventures.builder()
                    .name(searchParameters.getName())
                    .world(searchParameters.getWorld())
                    .persona(searchParameters.getPersona())
                    .ownerDiscordId(searchParameters.getOwnerDiscordId())
                    .favorites(searchParameters.isFavorites())
                    .multiplayer(searchParameters.isMultiplayer())
                    .page(searchParameters.getPage())
                    .size(searchParameters.getSize())
                    .model(getModel(searchParameters.getModel()))
                    .gameMode(getGameMode(searchParameters.getGameMode()))
                    .moderation(getModeration(searchParameters.getModeration()))
                    .sortingField(getSortingField(searchParameters.getSortingField()))
                    .direction(getDirection(searchParameters.getDirection()))
                    .visibility(getVisibility(searchParameters.getVisibility()))
                    .operation(getOperation(searchParameters.getOperation()))
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{adventureId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<AdventureResponse> getAdventureById(
            @PathVariable(required = true) String adventureId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetAdventureById query = GetAdventureById.build(adventureId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreateAdventureResponse> createAdventure(
            @Valid @RequestBody CreateAdventureRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            CreateAdventure command = requestMapper.toCommand(request, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @PutMapping("/{adventureId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdateAdventureResponse> updateAdventure(
            @PathVariable(required = true) String adventureId,
            @Valid @RequestBody UpdateAdventureRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            UpdateAdventure command = requestMapper.toCommand(request, adventureId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @DeleteMapping("/{adventureId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deleteAdventure(
            @PathVariable(required = true) String adventureId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeleteAdventure command = requestMapper.toCommand(adventureId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @PostMapping("/favorite")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> addFavoriteAdventure(@RequestBody FavoriteRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            AddFavoriteAdventure command = AddFavoriteAdventure.builder()
                    .assetId(request.getAssetId())
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @DeleteMapping("/favorite/{assetId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> removeFavoriteAdventure(@PathVariable(required = true) String assetId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            RemoveFavoriteAdventure command = RemoveFavoriteAdventure.builder()
                    .assetId(assetId)
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    private String getModel(SearchModel searchModel) {

        if (searchModel != null) {
            return toCamelCase(searchModel.name(), false, '_');
        }

        return EMPTY;
    }

    private String getGameMode(SearchGameMode searchGameMode) {

        if (searchGameMode != null) {
            return toCamelCase(searchGameMode.name(), false, '_');
        }

        return EMPTY;
    }

    private String getModeration(SearchModeration searchModeration) {

        if (searchModeration != null) {
            return toCamelCase(searchModeration.name(), false, '_');
        }

        return EMPTY;
    }

    private String getSortingField(SearchSortingField searchSortingField) {

        if (searchSortingField != null) {
            return toCamelCase(searchSortingField.name(), false, '_');
        }

        return EMPTY;
    }

    private String getDirection(SearchDirection searchDirection) {

        if (searchDirection != null) {
            return toCamelCase(searchDirection.name(), false, '_');
        }

        return EMPTY;
    }

    private String getVisibility(SearchVisibility searchVisibility) {

        if (searchVisibility != null) {
            return toCamelCase(searchVisibility.name(), false, '_');
        }

        return EMPTY;
    }

    private String getOperation(SearchOperation searchOperation) {

        if (searchOperation != null) {
            return toCamelCase(searchOperation.name(), false, '_');
        }

        return EMPTY;
    }
}
