package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.application.command.persona.CreatePersona;
import es.thalesalv.chatrpg.core.application.command.persona.DeletePersona;
import es.thalesalv.chatrpg.core.application.command.persona.UpdatePersona;
import es.thalesalv.chatrpg.core.application.query.persona.GetPersonaById;

public interface PersonaDomainService {

    Persona getPersonaById(GetPersonaById query);

    Persona getPersonaById(String id);

    Persona createFrom(CreatePersona command);

    Persona update(UpdatePersona command);

    void deletePersona(DeletePersona command);
}
