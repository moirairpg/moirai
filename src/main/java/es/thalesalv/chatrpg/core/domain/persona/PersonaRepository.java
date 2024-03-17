package es.thalesalv.chatrpg.core.domain.persona;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasWithReadAccess;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasWithWriteAccess;

public interface PersonaRepository {

    Optional<Persona> findById(String id, String requesterDiscordId);

    Persona save(Persona persona);

    void deleteById(String id);

    SearchPersonasResult searchPersonasWithReadAccess(SearchPersonasWithReadAccess query, String requesterDiscordId);

    SearchPersonasResult searchPersonasWithWriteAccess(SearchPersonasWithWriteAccess query, String requesterDiscordId);
}