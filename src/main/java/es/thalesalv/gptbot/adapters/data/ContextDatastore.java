package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;

@Component
public class ContextDatastore {

    private ThreadLocal<MessageEventData> messageEventData = new ThreadLocal<>();
    private ThreadLocal<Persona> persona = new ThreadLocal<>();

    public void setPersona(final Persona persona) {
        this.persona.set(persona);
    }

    public Persona getPersona() {
        return this.persona.get();
    }

    public boolean isPersonaNull() {
        return Objects.isNull(persona.get());
    }

    public void setMessageEventData(final MessageEventData messageEventData) {
        this.messageEventData.set(messageEventData);
    }

    public MessageEventData getMessageEventData() {
        return this.messageEventData.get();
    }

    public boolean isMessageEventDataNull() {
        return Objects.isNull(messageEventData.get());
    }

    public void clearContext() {
        this.persona.remove();
        this.messageEventData.remove();
    }
}
