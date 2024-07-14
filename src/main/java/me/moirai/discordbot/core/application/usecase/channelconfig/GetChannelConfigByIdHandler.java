package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigService;

@UseCaseHandler
public class GetChannelConfigByIdHandler extends AbstractUseCaseHandler<GetChannelConfigById, GetChannelConfigResult> {

    private final ChannelConfigService domainService;

    public GetChannelConfigByIdHandler(ChannelConfigService domainService) {
        this.domainService = domainService;
    }

    @Override
    public GetChannelConfigResult execute(GetChannelConfigById query) {

        ChannelConfig channelConfig = domainService.getChannelConfigById(query);
        return mapResult(channelConfig);
    }

    private GetChannelConfigResult mapResult(ChannelConfig channelConfig) {

        return GetChannelConfigResult.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .worldId(channelConfig.getWorldId())
                .personaId(channelConfig.getPersonaId())
                .visibility(channelConfig.getVisibility().name())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().name())
                .moderation(channelConfig.getModeration().name())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creationDate(channelConfig.getCreationDate())
                .lastUpdateDate(channelConfig.getLastUpdateDate())
                .build();
    }
}
