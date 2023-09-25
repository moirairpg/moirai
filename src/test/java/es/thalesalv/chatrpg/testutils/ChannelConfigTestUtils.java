package es.thalesalv.chatrpg.testutils;

import java.util.ArrayList;
import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;

public class ChannelConfigTestUtils {

    private static final String NANO_ID = "241OZASGM6CESV7";

    public static ChannelConfig buildChannelConfig() {

        return ChannelConfig.builder()
                .id(NANO_ID)
                .name("Test Asset")
                .persona(PersonaTestUtils.buildSimplePublicPersona())
                .world(WorldTestUtils.buildSimplePublicWorld())
                .build();
    }

    public static ChannelConfigEntity buildChannelConfigEntity() {

        return ChannelConfigEntity.builder()
                .id(NANO_ID)
                .name("Test Asset")
                .persona(PersonaTestUtils.buildSimplePublicPersonaEntity())
                .world(WorldTestUtils.buildSimplePublicWorldEntity())
                .build();
    }

    public static List<ChannelConfig> createList(final int amountOfItems) {

        final List<ChannelConfig> channelConfigs = new ArrayList<>();
        for (int i = 1; i < amountOfItems; i++) {
            channelConfigs.add(buildChannelConfig());
        }

        return channelConfigs;
    }

    public static List<ChannelConfigEntity> createEntityList(final int amountOfItems) {

        final List<ChannelConfigEntity> channelConfigs = new ArrayList<>();
        for (int i = 1; i < amountOfItems; i++) {
            channelConfigs.add(buildChannelConfigEntity());
        }

        return channelConfigs;
    }
}
