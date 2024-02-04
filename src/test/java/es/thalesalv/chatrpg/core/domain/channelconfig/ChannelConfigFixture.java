package es.thalesalv.chatrpg.core.domain.channelconfig;

import es.thalesalv.chatrpg.core.domain.PermissionsFixture;
import es.thalesalv.chatrpg.core.domain.Visibility;

public class ChannelConfigFixture {

    public static ChannelConfig.Builder sample() {

        // Given
        ChannelConfig.Builder channelConfigBuilder = ChannelConfig.builder();
        channelConfigBuilder.id("CHCONFID");
        channelConfigBuilder.name("Name");
        channelConfigBuilder.worldId("WRLDID");
        channelConfigBuilder.personaId("PRSNID");
        channelConfigBuilder.moderation(Moderation.STRICT);
        channelConfigBuilder.visibility(Visibility.fromString("PRIVATE"));
        channelConfigBuilder.modelConfiguration(ModelConfigurationFixture.sample().build());
        channelConfigBuilder.permissions(PermissionsFixture.samplePermissions().build());

        return channelConfigBuilder;
    }
}
