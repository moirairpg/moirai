package es.thalesalv.chatrpg.core.application.query.channelconfig;

import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.common.usecases.UseCaseHandler;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigDomainService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class GetChannelConfigByIdHandler extends UseCaseHandler<GetChannelConfigById, GetChannelConfigResult> {

    private final ChannelConfigDomainService domainService;

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
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
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
