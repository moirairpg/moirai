package es.thalesalv.gptbot.adapters.data;

import java.util.Objects;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.Persona;

@Component
public class ContextDatastore {

    public ThreadLocal<Persona> persona = new ThreadLocal<>();

    public void setPersona(final Persona persona) {
        this.persona.set(persona);
    }

    public Persona getPersona() {
        return this.persona.get();
    }

    public boolean isPersonaNull() {
        return Objects.isNull(persona.get());
    }

    public void clearContext() {
        this.persona.remove();
    }
}
