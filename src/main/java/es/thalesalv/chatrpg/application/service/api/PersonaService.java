package es.thalesalv.chatrpg.application.service.api;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final PersonaEntityToDTO personaEntityToDTO;

    private final PersonaRepository personaRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public List<Persona> retrieveAllPersonas() {

        LOGGER.debug("Retrieving persona data from request");
        return personaRepository.findAll()
                .stream()
                .map(personaEntityToDTO)
                .toList();
    }

    public Persona retrievePersonaById(final String personaId) {

        LOGGER.debug("Retrieving persona by ID data from request");
        return personaRepository.findById(personaId)
                .map(personaEntityToDTO)
                .orElseThrow(PersonaNotFoundException::new);
    }

    public Persona savePersona(final Persona persona) {

        LOGGER.debug("Saving persona data from request");
        return Optional.of(personaDTOToEntity.apply(persona))
                .map(personaRepository::save)
                .map(personaEntityToDTO)
                .orElseThrow();
    }

    public Persona updatePersona(final String personaId, final Persona persona) {

        LOGGER.debug("Updating persona data from request");
        return Optional.of(personaDTOToEntity.apply(persona))
                .map(c -> {
                    c.setId(personaId);
                    return personaRepository.save(c);
                })
                .map(personaEntityToDTO)
                .orElseThrow();
    }

    public void deletePersona(final String personaId) {

        LOGGER.debug("Deleting persona data from request");
        personaRepository.deleteById(personaId);
    }
}
