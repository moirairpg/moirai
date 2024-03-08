package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;

public interface PersonaDomainService {

    Persona createFrom(CreatePersona command);

    Persona update(UpdatePersona command);
}
