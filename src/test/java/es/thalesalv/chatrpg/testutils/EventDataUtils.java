package es.thalesalv.chatrpg.testutils;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;

public class EventDataUtils {

    public static EventData buildEventData() {

        final Channel channelDefinitions = Channel.builder()
                .channelConfig(ChannelConfigUtils.buildChannelConfig())
                .build();

        return EventData.builder()
                .channelDefinitions(channelDefinitions)
                .build();
    }
}
