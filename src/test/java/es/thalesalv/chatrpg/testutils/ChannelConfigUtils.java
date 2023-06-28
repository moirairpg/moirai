package es.thalesalv.chatrpg.testutils;

import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;

public class ChannelConfigUtils {

    public static ChannelConfig buildChannelConfig() {

        return ChannelConfig.builder()
                .persona(PersonaTestUtils.buildSimplePublicPersona())
                .world(WorldTestUtils.buildSimplePublicWorld())
                .build();
    }
}
