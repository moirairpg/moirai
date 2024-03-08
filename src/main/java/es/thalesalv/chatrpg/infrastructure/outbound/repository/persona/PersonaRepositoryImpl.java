package es.thalesalv.chatrpg.infrastructure.outbound.repository.persona;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonas;
import es.thalesalv.chatrpg.core.application.query.persona.SearchPersonasResult;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonaRepositoryImpl implements PersonaRepository {

    @Override
    public Persona save(Persona persona) {

        return persona;
    }

    @Override
    public Optional<Persona> findById(String id) {

        return Optional.empty();
    }

    @Override
    public void deleteById(String id) {

    }

    @Override
    public SearchPersonasResult searchPersonas(SearchPersonas query) {

        return null;
    }
}
