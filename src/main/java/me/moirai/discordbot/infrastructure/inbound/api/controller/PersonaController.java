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
import me.moirai.discordbot.core.application.usecase.persona.request.AddFavoritePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.CreatePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.DeletePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.GetPersonaById;
import me.moirai.discordbot.core.application.usecase.persona.request.RemoveFavoritePersona;
import me.moirai.discordbot.core.application.usecase.persona.request.SearchPersonas;
import me.moirai.discordbot.core.application.usecase.persona.request.UpdatePersona;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.PersonaRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.PersonaResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreatePersonaRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.PersonaSearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdatePersonaRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchDirection;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchOperation;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchSortingField;
import me.moirai.discordbot.infrastructure.inbound.api.request.enums.SearchVisibility;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreatePersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.PersonaResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchPersonasResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdatePersonaResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persona")
@Tag(name = "Personas", description = "Endpoints for managing MoirAI Personas")
public class PersonaController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final PersonaRequestMapper requestMapper;
    private final PersonaResponseMapper responseMapper;

    public PersonaController(UseCaseRunner useCaseRunner,
            PersonaRequestMapper requestMapper,
            PersonaResponseMapper responseMapper) {

        this.useCaseRunner = useCaseRunner;
        this.requestMapper = requestMapper;
        this.responseMapper = responseMapper;
    }

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchPersonasResponse> search(PersonaSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchPersonas query = SearchPersonas.builder()
                    .name(searchParameters.getName())
                    .ownerDiscordId(searchParameters.getOwnerDiscordId())
                    .favorites(searchParameters.isFavorites())
                    .page(searchParameters.getPage())
                    .size(searchParameters.getSize())
                    .sortingField(getSortingField(searchParameters.getSortingField()))
                    .direction(getDirection(searchParameters.getDirection()))
                    .visibility(getVisibility(searchParameters.getVisibility()))
                    .operation(getOperation(searchParameters.getOperation()))
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<PersonaResponse> getPersonaById(@PathVariable(required = true) String personaId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetPersonaById query = GetPersonaById.build(personaId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreatePersonaResponse> createPersona(@Valid @RequestBody CreatePersonaRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            CreatePersona command = requestMapper.toCommand(request, authenticatedUser.getId());
            return useCaseRunner.run(command);
        }).map(responseMapper::toResponse);
    }

    @PutMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdatePersonaResponse> updatePersona(
            @PathVariable(required = true) String personaId,
            @Valid @RequestBody UpdatePersonaRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            UpdatePersona command = requestMapper.toCommand(request, personaId,
                    authenticatedUser.getId());

            return useCaseRunner.run(command);
        }).map(responseMapper::toResponse);
    }

    @DeleteMapping("/{personaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deletePersona(@PathVariable(required = true) String personaId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeletePersona command = requestMapper.toCommand(personaId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @PostMapping("/favorite")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> addFavoritePersona(@RequestBody FavoriteRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            AddFavoritePersona command = AddFavoritePersona.builder()
                    .assetId(request.getAssetId())
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }

    @DeleteMapping("/favorite/{assetId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> removeFavoritePersona(@PathVariable(required = true) String assetId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            RemoveFavoritePersona command = RemoveFavoritePersona.builder()
                    .assetId(assetId)
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
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
