package es.thalesalv.chatrpg.core.domain.persona;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.request.SearchPersonasWithWriteAccess;
import es.thalesalv.chatrpg.core.application.usecase.persona.result.SearchPersonasResult;

public interface PersonaRepository {

    Optional<Persona> findById(String id);

    Persona save(Persona persona);

    void deleteById(String id);

    SearchPersonasResult searchPersonasWithReadAccess(SearchPersonasWithReadAccess query);

    SearchPersonasResult searchPersonasWithWriteAccess(SearchPersonasWithWriteAccess query);
}