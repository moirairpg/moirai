package me.moirai.discordbot.core.application.usecase.channelconfig.request;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;

public class CreateChannelConfigFixture {

    public static CreateChannelConfig.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();
        return CreateChannelConfig.builder()
                .name(channelConfig.getName())
                .personaId(channelConfig.getPersonaId())
                .worldId(channelConfig.getWorldId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .aiModel(channelConfig.getModelConfiguration().getAiModel().toString())
                .logitBias(channelConfig.getModelConfiguration().getLogitBias())
                .stopSequences(channelConfig.getModelConfiguration().getStopSequences())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .maxTokenLimit(channelConfig.getModelConfiguration().getMaxTokenLimit())
                .moderation("strict")
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .temperature(1.7)
                .gameMode(channelConfig.getGameMode().name())
                .visibility("private");
    }
}
