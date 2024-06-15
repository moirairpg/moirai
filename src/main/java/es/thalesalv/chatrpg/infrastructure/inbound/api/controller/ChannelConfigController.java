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
import es.thalesalv.chatrpg.common.web.SecurityContextAware;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.CreateChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.GetChannelConfigById;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.ChannelConfigRequestMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.mapper.ChannelConfigResponseMapper;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.SearchParameters;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.ChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.UpdateChannelConfigResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/channel-config")
@RequiredArgsConstructor
@Tag(name = "Channel Configs", description = "Endpoints for managing ChatRPG Channel Configs")
public class ChannelConfigController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigResponseMapper responseMapper;
    private final ChannelConfigRequestMapper requestMapper;

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchChannelConfigsResponse> searchChannelConfigsWithReadAccess(SearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/search/own")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchChannelConfigsResponse> searchChannelConfigsWithWriteAccess(SearchParameters searchParameters,
            Authentication authentication) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
                    .page(searchParameters.getPage())
                    .items(searchParameters.getItems())
                    .sortByField(searchParameters.getSortByField())
                    .direction(searchParameters.getDirection())
                    .name(searchParameters.getName())
                    .requesterDiscordId(authenticatedUser.getId())
                    .build();

            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @GetMapping("/{channelConfigId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<ChannelConfigResponse> getChannelConfigById(
            @PathVariable(name = "channelConfigId", required = true) String channelConfigId) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            GetChannelConfigById query = GetChannelConfigById.build(channelConfigId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(query));
        });
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<CreateChannelConfigResponse> createChannelConfig(
            @Valid @RequestBody CreateChannelConfigRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            CreateChannelConfig command = requestMapper.toCommand(request, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @PutMapping("/{channelConfigId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<UpdateChannelConfigResponse> updateChannelConfig(
            @PathVariable(name = "channelConfigId", required = true) String channelConfigId,
            @Valid @RequestBody UpdateChannelConfigRequest request) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            UpdateChannelConfig command = requestMapper.toCommand(request, channelConfigId, authenticatedUser.getId());
            return responseMapper.toResponse(useCaseRunner.run(command));
        });
    }

    @DeleteMapping("/{channelConfigId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<Void> deleteChannelConfig(
            @PathVariable(name = "channelConfigId", required = true) String channelConfigId) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            DeleteChannelConfig command = requestMapper.toCommand(channelConfigId, authenticatedUser.getId());
            useCaseRunner.run(command);

            return Mono.empty();
        });
    }
}
