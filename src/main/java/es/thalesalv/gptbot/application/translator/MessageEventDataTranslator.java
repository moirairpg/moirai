package es.thalesalv.gptbot.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.MessageEventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Component
@RequiredArgsConstructor
public class MessageEventDataTranslator {

    public MessageEventData translate(final MessageReceivedEvent event) {

        return MessageEventData.builder()
                .bot(event.getJDA().getSelfUser())
                .messageAuthor(event.getAuthor())
                .message(event.getMessage())
                .channel(event.getChannel())
                .guild(event.getGuild())
                .build();
    }
}
