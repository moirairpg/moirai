package es.thalesalv.chatrpg.application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
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

    private static final String PERSONA_ID_NOT_FOUND = "persona with id PERSONA_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public List<Persona> retrieveAllPersonas(final String userId) {

        LOGGER.debug("Entering retrieveAllPersonas. userId -> {}", userId);
        return personaRepository.findAll()
                .stream()
                .filter(p -> filterReadPermissions(p, userId))
                .map(personaEntityToDTO)
                .toList();
    }

    public Persona retrievePersonaById(final String personaId, final String userId) {

        LOGGER.debug("Entering retrievePersonaById. personaId -> {}, userId -> {}", personaId, userId);
        return personaRepository.findById(personaId)
                .filter(p -> filterReadPermissions(p, userId))
                .map(personaEntityToDTO)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Error retrieving persona by id: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId)));
    }

    public Persona savePersona(final Persona persona) {

        LOGGER.debug("Entering savePersona. persona -> {}", persona);
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

    public Persona updatePersona(final String personaId, final Persona persona, final String userId) {

        LOGGER.debug("Entering updatePersona. personaId -> {}, userId -> {}, persona -> {}", personaId, userId,
                persona);

        return Optional.of(personaDTOToEntity.apply(persona))
                .map(p -> {
                    if (!filterWritePermissions(p, userId)) {
                        throw new InsufficientPermissionException("Not enough permissions to modify this persona");
                    }

                    p.setId(personaId);
                    p.setOwner(Optional.ofNullable(p.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    return personaRepository.save(p);
                })
                .map(personaEntityToDTO)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Error updating persona: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId)));
    }

    public void deletePersona(final String personaId, final String userId) {

        LOGGER.debug("Entering deletePersona. personaId -> {}, userId -> {}", personaId, userId);
        personaRepository.findById(personaId)
                .ifPresent(persona -> {
                    if (!filterWritePermissions(persona, userId)) {
                        throw new InsufficientPermissionException("Not enough permissions to delete this persona");
                    }

                    personaRepository.delete(persona);
                });
    }

    private boolean filterReadPermissions(final PersonaEntity persona, final String userId) {

        final boolean isPublic = Visibility.isPublic(persona.getVisibility());
        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final boolean canRead = persona.getReadPermissions()
                .contains(userId)
                || persona.getWritePermissions()
                        .contains(userId);

        return isPublic || (isOwner || canRead);
    }

    private boolean filterWritePermissions(final PersonaEntity persona, final String userId) {

        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final boolean canWrite = persona.getWritePermissions()
                .contains(userId);

        return isOwner || canWrite;
    }
}
