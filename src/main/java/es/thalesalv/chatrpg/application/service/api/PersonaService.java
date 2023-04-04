package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;

import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final PersonaEntityToDTO personaEntityToDTO;

    private final PersonaRepository personaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public Mono<List<Persona>> retrieveAllPersonas() {

        LOGGER.debug("Retrieving persona data from request");
        return Mono.just(personaRepository.findAll())
                .map(personas -> personas.stream()
                        .map(personaEntityToDTO)
                        .toList());
    }

    public Mono<List<Persona>> retrievePersonaById(final String personaId) {

        LOGGER.debug("Retrieving persona by ID data from request");
        return Mono.just(personaRepository.findById(personaId)
                .orElseThrow(PersonaNotFoundException::new))
                .map(personaEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<Persona>> savePersona(final Persona persona) {

        LOGGER.debug("Saving persona data from request");
        return Mono.just(personaDTOToEntity.apply(persona))
                .map(personaRepository::save)
                .map(personaEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<Persona>> updatePersona(final String personaId, final Persona persona) {

        LOGGER.debug("Updating persona data from request");
        return Mono.just(personaDTOToEntity.apply(persona))
                .map(c -> {
                    c.setId(personaId);
                    return personaRepository.save(c);
                })
                .map(personaEntityToDTO)
                .map(Arrays::asList);
    }

    public void deletePersona(final String personaId) {

        LOGGER.debug("Deleting persona data from request");
        personaRepository.deleteById(personaId);
    }
}
