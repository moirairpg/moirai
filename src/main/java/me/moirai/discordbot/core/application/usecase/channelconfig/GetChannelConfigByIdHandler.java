package me.moirai.discordbot.core.application.usecase.channelconfig;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetAccessDeniedException;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.ChannelConfigQueryRepository;
import me.moirai.discordbot.core.application.usecase.channelconfig.request.GetChannelConfigById;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;

@UseCaseHandler
public class GetChannelConfigByIdHandler extends AbstractUseCaseHandler<GetChannelConfigById, GetChannelConfigResult> {

    private static final String CHANNEL_CONFIG_NOT_FOUND = "Channel config to be viewed was not found";
    private static final String PERMISSION_VIEW_DENIED = "User does not have permission to view this channel config";

    private final ChannelConfigQueryRepository queryRepository;

    public GetChannelConfigByIdHandler(ChannelConfigQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public GetChannelConfigResult execute(GetChannelConfigById query) {

        ChannelConfig channelConfig = queryRepository.findById(query.getId())
                .orElseThrow(() -> new AssetNotFoundException(CHANNEL_CONFIG_NOT_FOUND));

        if (!channelConfig.canUserRead(query.getRequesterDiscordId())) {
            throw new AssetAccessDeniedException(PERMISSION_VIEW_DENIED);
        }

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
