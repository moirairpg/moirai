package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class CreateChannelConfigRequestFixture {

    public static CreateChannelConfigRequest.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return CreateChannelConfigRequest.builder()
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .moderation(channelConfig.getModeration().name())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .visibility(channelConfig.getVisibility().name());
    }
}
