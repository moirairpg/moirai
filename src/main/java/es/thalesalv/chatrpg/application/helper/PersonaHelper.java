package es.thalesalv.chatrpg.application.helper;

import java.util.List;

import es.thalesalv.chatrpg.application.util.StringProcessor;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;

public interface PersonaHelper<T> {

    List<T> formatNudge(final Persona persona, final List<T> messages, final StringProcessor inputProcessor);

    List<T> formatBump(final Persona persona, final List<T> messages, final StringProcessor inputProcessor);
}
