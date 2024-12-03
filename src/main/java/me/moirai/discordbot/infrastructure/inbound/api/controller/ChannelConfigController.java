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
import me.moirai.discordbot.core.application.usecase.channelconfig.request.AddFavoriteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.CreateChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.DeleteChannelConfig;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithReadAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchChannelConfigsWithWriteAccess;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.SearchFavoriteChannelConfigs;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.UpdateChannelConfig;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.ChannelConfigRequestMapper;
import me.moirai.discordbot.infrastructure.inbound.api.mapper.ChannelConfigResponseMapper;
import me.moirai.discordbot.infrastructure.inbound.api.request.ChannelConfigSearchParameters;
import me.moirai.discordbot.infrastructure.inbound.api.request.CreateChannelConfigRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.FavoriteRequest;
import me.moirai.discordbot.infrastructure.inbound.api.request.UpdateChannelConfigRequest;
import me.moirai.discordbot.infrastructure.inbound.api.response.ChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateChannelConfigResponse;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/channel-config")
@Tag(name = "Channel Configs", description = "Endpoints for managing MoirAI Channel Configs")
public class ChannelConfigController extends SecurityContextAware {

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigResponseMapper responseMapper;
    private final ChannelConfigRequestMapper requestMapper;

    public ChannelConfigController(UseCaseRunner useCaseRunner,
            ChannelConfigResponseMapper responseMapper,
            ChannelConfigRequestMapper requestMapper) {

        this.useCaseRunner = useCaseRunner;
        this.responseMapper = responseMapper;
        this.requestMapper = requestMapper;
    }

    @GetMapping("/search")
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<SearchChannelConfigsResponse> searchChannelConfigsWithReadAccess(ChannelConfigSearchParameters searchParameters) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchChannelConfigsWithReadAccess query = SearchChannelConfigsWithReadAccess.builder()
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
    public Mono<SearchChannelConfigsResponse> searchChannelConfigsWithWriteAccess(ChannelConfigSearchParameters searchParameters,
            Authentication authentication) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchChannelConfigsWithWriteAccess query = SearchChannelConfigsWithWriteAccess.builder()
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
    public Mono<SearchChannelConfigsResponse> searchFavoriteChannelConfigs(ChannelConfigSearchParameters searchParameters,
            Authentication authentication) {

        return mapWithAuthenticatedUser(authenticatedUser -> {

            SearchFavoriteChannelConfigs query = SearchFavoriteChannelConfigs.builder()
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

    @PostMapping("/favorite")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Mono<Void> addFavoriteChannelConfig(@RequestBody FavoriteRequest request) {

        return flatMapWithAuthenticatedUser(authenticatedUser -> {

            AddFavoriteChannelConfig command = AddFavoriteChannelConfig.builder()
                    .assetId(request.getAssetId())
                    .playerDiscordId(authenticatedUser.getId())
                    .build();

            useCaseRunner.run(command);

            return Mono.empty();
        });
    }
}
