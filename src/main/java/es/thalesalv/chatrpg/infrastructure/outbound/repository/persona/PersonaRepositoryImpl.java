package es.thalesalv.chatrpg.infrastructure.outbound.repository.persona;

import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PersonaRepositoryImpl implements PersonaRepository {

    @Override
    public Persona save(Persona persona) {

        return persona;
    }
}
