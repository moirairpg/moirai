package es.thalesalv.chatrpg.application.mapper;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Component
@RequiredArgsConstructor
public class EventDataMapper {

    public EventData translate(final MessageReceivedEvent event, final Channel channelDefinitions) {

        return EventData.builder()
                .bot(event.getJDA()
                        .getSelfUser())
                .messageAuthor(event.getAuthor())
                .message(event.getMessage())
                .currentChannel(event.getChannel())
                .guild(event.getGuild())
                .channelDefinitions(channelDefinitions)
                .build();
    }

    public EventData translate(final SelfUser bot, final MessageChannelUnion channel, final Channel channelDefinitions,
            final Message message) {

        return EventData.builder()
                .bot(bot)
                .messageAuthor(message.getAuthor())
                .message(message)
                .currentChannel(channel)
                .channelDefinitions(channelDefinitions)
                .build();
    }
}
