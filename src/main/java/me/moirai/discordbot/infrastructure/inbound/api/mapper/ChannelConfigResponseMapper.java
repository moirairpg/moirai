package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.channelconfig.result.CreateChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.SearchChannelConfigsResult;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.UpdateChannelConfigResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.ChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.CreateChannelConfigResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.SearchChannelConfigsResponse;
import me.moirai.discordbot.infrastructure.inbound.api.response.UpdateChannelConfigResponse;

@Component
public class ChannelConfigResponseMapper {

    public SearchChannelConfigsResponse toResponse(SearchChannelConfigsResult result) {

        List<ChannelConfigResponse> worlds = result.getResults()
                .stream()
                .map(this::toResponse)
                .toList();

        return SearchChannelConfigsResponse.builder()
                .page(result.getPage())
                .resultsInPage(result.getItems())
                .totalPages(result.getTotalPages())
                .totalResults(result.getTotalItems())
                .results(worlds)
                .build();
    }

    public ChannelConfigResponse toResponse(GetChannelConfigResult result) {

        return ChannelConfigResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .worldId(result.getWorldId())
                .personaId(result.getPersonaId())
                .visibility(result.getVisibility())
                .aiModel(result.getAiModel())
                .moderation(result.getModeration())
                .maxTokenLimit(result.getMaxTokenLimit())
                .messageHistorySize(result.getMessageHistorySize())
                .temperature(result.getTemperature())
                .frequencyPenalty(result.getFrequencyPenalty())
                .presencePenalty(result.getPresencePenalty())
                .stopSequences(result.getStopSequences())
                .logitBias(result.getLogitBias())
                .usersAllowedToWrite(result.getUsersAllowedToWrite())
                .usersAllowedToRead(result.getUsersAllowedToRead())
                .discordChannelId(result.getDiscordChannelId())
                .build();
    }

    public CreateChannelConfigResponse toResponse(CreateChannelConfigResult result) {

        return CreateChannelConfigResponse.build(result.getId());
    }

    public UpdateChannelConfigResponse toResponse(UpdateChannelConfigResult result) {

        return UpdateChannelConfigResponse.build(result.getLastUpdatedDateTime());
    }
}
