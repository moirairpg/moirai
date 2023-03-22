package es.thalesalv.chatrpg.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.openai.dto.Channel;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Component
@RequiredArgsConstructor
public class MessageEventDataTranslator {

    public EventData translate(final MessageReceivedEvent event, final Channel channelConfig) {

        return EventData.builder()
                .bot(event.getJDA().getSelfUser())
                .messageAuthor(event.getAuthor())
                .message(event.getMessage())
                .channel(event.getChannel())
                .guild(event.getGuild())
                .botChannelDefinitions(channelConfig)
                .build();
    }

    public EventData translate(final SelfUser bot, final MessageChannelUnion channel, final Channel channelConfig, final Message message) {

        return EventData.builder()
                .bot(bot)
                .messageAuthor(message.getAuthor())
                .message(message)
                .channel(channel)
                .botChannelDefinitions(channelConfig)
                .build();
    }
}
