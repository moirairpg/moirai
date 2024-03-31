package es.thalesalv.chatrpg.core.application.command.channelconfig;

import java.util.Collections;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class UpdateChannelConfigFixture {

    public static UpdateChannelConfig.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return UpdateChannelConfig.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .worldId(channelConfig.getWorldId())
                .personaId(channelConfig.getPersonaId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .visibility(channelConfig.getVisibility().name())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().getInternalModelName())
                .moderation(channelConfig.getModeration().name())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .messageHistorySize(channelConfig.getModelConfiguration().getMessageHistorySize())
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .stopSequencesToAdd(channelConfig.getModelConfiguration().getStopSequences())
                .stopSequencesToRemove(channelConfig.getModelConfiguration().getStopSequences())
                .logitBiasToAdd(channelConfig.getModelConfiguration().getLogitBias())
                .logitBiasToRemove(Collections.singletonList("TKN"))
                .usersAllowedToWriteToAdd(Collections.singletonList("USRID"))
                .usersAllowedToWriteToRemove(Collections.singletonList("USRID"))
                .usersAllowedToReadToAdd(Collections.singletonList("USRID"))
                .usersAllowedToReadToRemove(Collections.singletonList("USRID"))
                .requesterDiscordId(channelConfig.getOwnerDiscordId());
    }
}
