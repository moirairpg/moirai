package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfig;
import es.thalesalv.chatrpg.core.domain.channelconfig.ChannelConfigFixture;

public class ChannelConfigEntityFixture {

    public static ChannelConfigEntity.Builder sample() {

        ChannelConfig channelConfig = ChannelConfigFixture.sample().build();

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .worldId(channelConfig.getWorldId())
                .personaId(channelConfig.getPersonaId())
                .discordChannelId(channelConfig.getDiscordChannelId())
                .moderation(channelConfig.getModeration().toString())
                .visibility(channelConfig.getVisibility().toString())
                .usersAllowedToRead(channelConfig.getUsersAllowedToRead())
                .usersAllowedToWrite(channelConfig.getUsersAllowedToWrite())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .creatorDiscordId(channelConfig.getCreatorDiscordId())
                .modelConfiguration(ModelConfigurationEntityFixture.gpt3516k().build());
    }
}
