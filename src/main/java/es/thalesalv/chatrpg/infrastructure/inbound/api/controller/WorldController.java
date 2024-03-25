package es.thalesalv.chatrpg.infrastructure.inbound.api.controller;

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

import es.thalesalv.chatrpg.common.usecases.UseCaseRunner;
import es.thalesalv.chatrpg.core.application.command.world.CreateWorld;
import es.thalesalv.chatrpg.core.application.command.world.DeleteWorld;
import es.thalesalv.chatrpg.core.application.command.world.UpdateWorld;
import es.thalesalv.chatrpg.core.application.query.world.GetWorldById;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldsWithWriteAccess;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.WorldResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.SearchParameters;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateWorldRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateWorldResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchWorldsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateWorldResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.WorldResponse;
import es.thalesalv.chatrpg.infrastructure.security.authentication.DiscordPrincipal;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/world")
@RequiredArgsConstructor
@Tag(name = "Worlds", description = "Endpoints for managing ChatRPG Worlds")
public class WorldController {

    private final UseCaseRunner useCaseRunner;
    private final WorldResponseMapper responseMapper;
    private final WorldRequestMapper requestMapper;

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public SearchWorldsResponse seacrhWorldsWithReadAccess(SearchParameters searchParameters, Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        SearchWorldsWithReadAccess query = SearchWorldsWithReadAccess.builder()
                .page(searchParameters.getPage())
                .items(searchParameters.getItems())
                .sortByField(searchParameters.getSortByField())
                .direction(searchParameters.getDirection())
                .name(searchParameters.getName())
                .requesterDiscordId(principal.getId())
                .build();

        return responseMapper.toResponse(useCaseRunner.run(query));
    }

    @GetMapping("/search/own")
    @ResponseStatus(code = HttpStatus.OK)
    public SearchWorldsResponse seacrhWorldsWithWriteAccess(SearchParameters searchParameters, Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        SearchWorldsWithWriteAccess query = SearchWorldsWithWriteAccess.builder()
                .page(searchParameters.getPage())
                .items(searchParameters.getItems())
                .sortByField(searchParameters.getSortByField())
                .direction(searchParameters.getDirection())
                .name(searchParameters.getName())
                .requesterDiscordId(principal.getId())
                .build();

        return responseMapper.toResponse(useCaseRunner.run(query));
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public WorldResponse getWorldById(@PathVariable("id") String id, Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        GetWorldById query = GetWorldById.build(id, principal.getId());

        return responseMapper.toResponse(useCaseRunner.run(query));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CreateWorldResponse createWorld(@Valid @RequestBody CreateWorldRequest request,
            Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        CreateWorld command = requestMapper.toCommand(request, principal.getId());

        return responseMapper.toResponse(useCaseRunner.run(command));
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UpdateWorldResponse updateWorld(@PathVariable("id") String id,
            @Valid @RequestBody UpdateWorldRequest request, Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        UpdateWorld command = requestMapper.toCommand(request, id, principal.getId());

        return responseMapper.toResponse(useCaseRunner.run(command));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public void deleteWorld(@PathVariable("id") String id, Authentication authentication) {

        DiscordPrincipal principal = (DiscordPrincipal) authentication.getPrincipal();
        DeleteWorld command = requestMapper.toCommand(id, principal.getId());

        useCaseRunner.run(command);
    }
}
