package es.thalesalv.chatrpg.testutils;

import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;

public class ChannelConfigTestUtils {

    private static final String NANO_ID = "241OZASGM6CESV7";

    public static ChannelConfig buildChannelConfig() {

        return ChannelConfig.builder()
                .id(NANO_ID)
                .persona(PersonaTestUtils.buildSimplePublicPersona())
                .world(WorldTestUtils.buildSimplePublicWorld())
                .build();
    }
}
