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
                .moderation(channelConfig.getModeration().toString())
                .visibility(channelConfig.getVisibility().toString())
                .usersAllowedToRead(channelConfig.getReaderUsers())
                .usersAllowedToWrite(channelConfig.getWriterUsers())
                .ownerDiscordId(channelConfig.getOwnerDiscordId())
                .modelConfiguration(ModelConfigurationEntityFixture.gpt3516k().build());
    }
}
