package me.moirai.discordbot.core.application.usecase.channelconfig.request;

import java.util.Collections;

import org.assertj.core.util.Maps;

import me.moirai.discordbot.core.domain.channelconfig.ChannelConfig;
import me.moirai.discordbot.core.domain.channelconfig.ChannelConfigFixture;

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
                .temperature(channelConfig.getModelConfiguration().getTemperature())
                .frequencyPenalty(channelConfig.getModelConfiguration().getFrequencyPenalty())
                .presencePenalty(channelConfig.getModelConfiguration().getPresencePenalty())
                .stopSequencesToAdd(channelConfig.getModelConfiguration().getStopSequences())
                .stopSequencesToRemove(channelConfig.getModelConfiguration().getStopSequences())
                .logitBiasToAdd(Maps.newHashMap("TKNID", 99D))
                .logitBiasToRemove(Collections.singletonList("TKN"))
                .usersAllowedToWriteToAdd(Collections.singletonList("USRID"))
                .usersAllowedToWriteToRemove(Collections.singletonList("USRID"))
                .usersAllowedToReadToAdd(Collections.singletonList("USRID"))
                .usersAllowedToReadToRemove(Collections.singletonList("USRID"))
                .gameMode(channelConfig.getGameMode().name())
                .requesterDiscordId(channelConfig.getOwnerDiscordId());
    }
}
