package es.thalesalv.chatrpg.testutils;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.Channel;
import net.dv8tion.jda.api.entities.SelfUser;

public class EventDataUtils {

    public static EventData buildEventData() {

        final Channel channelDefinitions = Channel.builder()
                .channelConfig(ChannelConfigTestUtils.buildChannelConfig())
                .build();

        return EventData.builder()
                .channelDefinitions(channelDefinitions)
                .build();
    }

    public static EventData buildEventData(final SelfUser bot) {

        final Channel channelDefinitions = Channel.builder()
                .channelConfig(ChannelConfigTestUtils.buildChannelConfig())
                .build();

        return EventData.builder()
                .channelDefinitions(channelDefinitions)
                .bot(bot)
                .build();
    }
}
