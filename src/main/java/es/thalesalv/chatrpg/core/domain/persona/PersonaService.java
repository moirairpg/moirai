package es.thalesalv.chatrpg.core.domain.persona;

import es.thalesalv.chatrpg.core.application.usecase.persona.request.CreatePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.DeletePersona;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.GetPersonaById;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.UpdatePersona;
import reactor.core.publisher.Mono;

public interface PersonaService {

    Persona getPersonaById(GetPersonaById query);

    Persona getPersonaById(String id);

    Mono<Persona> createFrom(CreatePersona command);

    Mono<Persona> update(UpdatePersona command);

    void deletePersona(DeletePersona command);
}
