package es.thalesalv.chatrpg.core.domain.persona;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonas;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;

public interface PersonaRepository {

    Optional<Persona> findById(String id);

    Persona save(Persona persona);

    void deleteById(String id);

    SearchPersonasResult searchPersonas(SearchPersonas query, String requesterDiscordId);
}