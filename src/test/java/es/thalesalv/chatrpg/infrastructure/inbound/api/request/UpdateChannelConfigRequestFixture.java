package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import java.util.Collections;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class UpdateChannelConfigRequestFixture {

    public static UpdateChannelConfigRequest.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return UpdateChannelConfigRequest.builder()
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .stopSequencesToAdd(channelConfig.getModelConfiguration().getStopSequences())
                .stopSequencesToRemove(channelConfig.getModelConfiguration().getStopSequences())
                .logitBiasToAdd(channelConfig.getModelConfiguration().getLogitBias())
                .logitBiasToRemove(Collections.singletonList("TKN"))
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .moderation(channelConfig.getModeration().name())
                .usersAllowedToWriteToAdd(Collections.singletonList("USRID"))
                .usersAllowedToWriteToRemove(Collections.singletonList("USRID"))
                .usersAllowedToReadToAdd(Collections.singletonList("USRID"))
                .usersAllowedToReadToRemove(Collections.singletonList("USRID"))
                .temperature(1.7)
                .visibility(channelConfig.getVisibility().name());
    }
}