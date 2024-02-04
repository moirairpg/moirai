package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;

public interface PersonaDomainService {

    Persona createFrom(CreatePersona command);
}
