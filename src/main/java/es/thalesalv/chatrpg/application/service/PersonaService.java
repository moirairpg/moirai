package es.thalesalv.chatrpg.application.service;

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
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final JDA jda;
    private final PersonaDTOToEntity personaDTOToEntity;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final PersonaRepository personaRepository;

    private static final String DEFAULT_ID = "0";
    private static final String PERSONA_ID_NOT_FOUND = "persona with id PERSONA_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public List<Persona> retrieveAllPersonas() {

        LOGGER.debug("Retrieving persona data from request");
        return personaRepository.findAll()
                .stream()
                .filter(l -> !l.getId().equals(DEFAULT_ID))
                .map(personaEntityToDTO)
                .toList();
    }

    public Persona retrievePersonaById(final String personaId) {

        LOGGER.debug("Retrieving persona by ID data from request");
        return personaRepository.findById(personaId)
                .map(personaEntityToDTO)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Error retrieving persona by id: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId)));
    }

    public Persona savePersona(final Persona persona) {

        LOGGER.debug("Saving persona data from request");
        return Optional.of(personaDTOToEntity.apply(persona))
                .map(personaEntity -> {
                    personaEntity.setOwner(Optional.ofNullable(personaEntity.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    return personaEntity;
                })
                .map(personaRepository::save)
                .map(personaEntityToDTO)
                .orElseThrow(() -> new RuntimeException("Error saving persona"));
    }

    public Persona updatePersona(final String personaId, final Persona persona) {

        LOGGER.debug("Updating persona data from request");
        return Optional.of(personaDTOToEntity.apply(persona))
                .map(c -> {
                    c.setId(personaId);
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    return personaRepository.save(c);
                })
                .map(personaEntityToDTO)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Error updating persona: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId)));
    }

    public void deletePersona(final String personaId) {

        LOGGER.debug("Deleting persona data from request");
        personaRepository.deleteById(personaId);
    }
}
