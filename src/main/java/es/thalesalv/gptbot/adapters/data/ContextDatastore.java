package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;
import java.util.Optional;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.CommandEventData;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;

@Component
public class ContextDatastore {

    private ThreadLocal<MessageEventData> messageEventData = new ThreadLocal<>();
    private ThreadLocal<CommandEventData> commandEventData = new ThreadLocal<>();
    private ThreadLocal<Persona> persona = new ThreadLocal<>();

    public void setPersona(final Persona persona) {
        this.persona.set(persona);
    }

    public Persona getPersona() {
        return Optional.ofNullable(this.persona)
        		.map(ThreadLocal::get)
        		.orElseThrow(() -> new NullPointerException("persona not set on thread"));
    }

    public boolean isPersonaNull() {
        return Objects.isNull(persona.get());
    }

    public void setMessageEventData(final MessageEventData messageEventData) {
        this.messageEventData.set(messageEventData);
    }

    public MessageEventData getMessageEventData() {
        return Optional.ofNullable(this.messageEventData)
        		.map(ThreadLocal::get)
        		.orElseThrow(()-> new NullPointerException("messageEventData not set on thread"));
    }

    public boolean isMessageEventDataNull() {
        return Objects.isNull(messageEventData.get());
    }

    public void setCommandEventData(final CommandEventData commandEventData) {
        this.commandEventData.set(commandEventData);
    }

    public CommandEventData getCommandEventData() {
        return Optional.ofNullable(this.commandEventData)
        		.map(ThreadLocal::get)
        		.orElseThrow(()-> new NullPointerException("commandEventData not set on thread"));
    }

    public boolean isCommandEventDataNull() {
        return Objects.isNull(commandEventData.get());
    }

    public void clearContext() {
        this.persona.remove();
        this.messageEventData.remove();
        this.commandEventData.remove();
    }
}
