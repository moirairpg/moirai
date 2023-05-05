package es.thalesalv.chatrpg.application.service;

import java.util.Collections;
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

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final PersonaRepository personaRepository;

    private static final String PERSONA_ID_NOT_FOUND = "persona with id PERSONA_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public List<Persona> retrieveAllPersonas(final String userId) {

        LOGGER.debug("Entering retrieveAllPersonas. userId -> {}", userId);
        return personaRepository.findAll()
                .stream()
                .filter(p -> hasReadPermissions(p, userId))
                .map(personaEntityToDTO)
                .toList();
    }

    public Persona savePersona(final Persona persona) {

        LOGGER.debug("Entering savePersona. persona -> {}", persona);
        final PersonaEntity personaEntity = personaDTOToEntity.apply(persona);
        return personaEntityToDTO.apply(personaRepository.save(personaEntity));
    }

    public Persona updatePersona(final String personaId, final Persona persona, final String userId) {

        LOGGER.debug("Entering updatePersona. personaId -> {}, userId -> {}, persona -> {}", personaId, userId,
                persona);

        personaRepository.findById(personaId)
                .orElseThrow(() -> new PersonaNotFoundException(
                        "Error updating persona: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId)));

        final PersonaEntity personaEntity = personaDTOToEntity.apply(persona);
        if (!hasWritePermissions(personaEntity, userId)) {
            throw new InsufficientPermissionException("Not enough permissions to modify this persona");
        }

        personaEntity.setId(personaId);
        return personaEntityToDTO.apply(personaRepository.save(personaEntity));
    }

    public void deletePersona(final String personaId, final String userId) {

        LOGGER.debug("Entering deletePersona. personaId -> {}, userId -> {}", personaId, userId);
        personaRepository.findById(personaId)
                .ifPresentOrElse(persona -> {
                    if (!hasWritePermissions(persona, userId)) {
                        throw new InsufficientPermissionException("Not enough permissions to delete this persona");
                    }

                    personaRepository.delete(persona);
                }, () -> {
                    throw new PersonaNotFoundException(
                            "Error deleting persona: " + PERSONA_ID_NOT_FOUND.replace("PERSONA_ID", personaId));
                });
    }

    private boolean hasReadPermissions(final PersonaEntity persona, final String userId) {

        final boolean isPublic = Visibility.isPublic(persona.getVisibility());
        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final List<String> readPermissions = Optional.ofNullable(persona.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(persona.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);
        return isPublic || (isOwner || canRead);
    }

    private boolean hasWritePermissions(final PersonaEntity persona, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(persona.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = persona.getOwner()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }
}