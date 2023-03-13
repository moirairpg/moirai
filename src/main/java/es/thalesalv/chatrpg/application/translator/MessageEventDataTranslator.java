package es.thalesalv.chatrpg.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.config.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Component
@RequiredArgsConstructor
public class MessageEventDataTranslator {

    public MessageEventData translate(final MessageReceivedEvent event, final Persona persona) {

        return MessageEventData.builder()
                .bot(event.getJDA().getSelfUser())
                .messageAuthor(event.getAuthor())
                .message(event.getMessage())
                .channel(event.getChannel())
                .guild(event.getGuild())
                .persona(persona)
                .build();
    }

    public MessageEventData translate(final SlashCommandInteractionEvent event, final Persona persona, final Message message) {

        return MessageEventData.builder()
                .bot(event.getJDA().getSelfUser())
                .messageAuthor(message.getAuthor())
                .message(message)
                .channel(event.getChannel())
                .guild(event.getGuild())
                .persona(persona)
                .build();
    }
}
