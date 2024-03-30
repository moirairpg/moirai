package es.thalesalv.chatrpg.infrastructure.inbound.api.response;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class ChannelConfigResponseFixture {

    public static ChannelConfigResponse.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return ChannelConfigResponse.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .moderation(channelConfig.getModeration().name())
                .usersAllowedToRead(channelConfig.getReaderUsers())
                .usersAllowedToWrite(channelConfig.getWriterUsers())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .visibility(channelConfig.getVisibility().name());
    }
}