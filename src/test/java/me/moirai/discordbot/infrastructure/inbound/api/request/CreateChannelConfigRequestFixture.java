package me.moirai.discordbot.infrastructure.inbound.api.request;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;

public class CreateChannelConfigRequestFixture {

    public static CreateChannelConfigRequest sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        CreateChannelConfigRequest request = new CreateChannelConfigRequest();

        request.setName(channelConfig.getName());
        request.setPersonaId(channelConfig.getPersonaId());
        request.setWorldId(channelConfig.getWorldId());
        request.setDiscordChannelId(channelConfig.getDiscordChannelId());
        request.setAiModel(channelConfig.getModelConfiguration().getAiModel().toString());
        request.setLogitBias(channelConfig.getModelConfiguration().getLogitBias());
        request.setStopSequences(channelConfig.getModelConfiguration().getStopSequences());
        request.setFrequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty());
        request.setMaxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit());
        request.setModeration(channelConfig.getModeration().name());
        request.setGameMode(channelConfig.getGameMode().name());
        request.setUsersAllowedToRead(channelConfig.getUsersAllowedToRead());
        request.setUsersAllowedToWrite(channelConfig.getUsersAllowedToWrite());
        request.setTemperature(channelConfig.getModelConfiguration().getTemperature());
        request.setVisibility(channelConfig.getVisibility().name());

        return request;
    }
}
