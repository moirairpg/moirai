package me.moirai.discordbot.infrastructure.inbound.api.request;

import java.util.Collections;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;

public class UpdateChannelConfigRequestFixture {

    public static UpdateChannelConfigRequest sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        UpdateChannelConfigRequest request = new UpdateChannelConfigRequest();

        request.setName(channelConfig.getName());
        request.setPersonaId(channelConfig.getPersonaId());
        request.setWorldId(channelConfig.getWorldId());
        request.setDiscordChannelId(channelConfig.getDiscordChannelId());
        request.setAiModel(channelConfig.getModelConfiguration().getAiModel().toString());
        request.setStopSequencesToAdd(channelConfig.getModelConfiguration().getStopSequences());
        request.setStopSequencesToRemove(channelConfig.getModelConfiguration().getStopSequences());
        request.setLogitBiasToAdd(channelConfig.getModelConfiguration().getLogitBias());
        request.setLogitBiasToRemove(Collections.singletonList("TKN"));
        request.setFrequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty());
        request.setMaxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit());
        request.setModeration(channelConfig.getModeration().name());
        request.setGameMode(channelConfig.getGameMode().name());
        request.setUsersAllowedToWriteToAdd(Collections.singletonList("USRID"));
        request.setUsersAllowedToWriteToRemove(Collections.singletonList("USRID"));
        request.setUsersAllowedToReadToAdd(Collections.singletonList("USRID"));
        request.setUsersAllowedToReadToRemove(Collections.singletonList("USRID"));
        request.setTemperature(1.7);
        request.setVisibility(channelConfig.getVisibility().name());
        request.setMultiplayer(false);

        return request;
    }
}