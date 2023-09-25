package es.thalesalv.chatrpg.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.PersonaEntityToDTO;
import es.thalesalv.chatrpg.domain.criteria.AssetSpecification;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.PersonaNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.PagedResponse;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.domain.model.discord.DiscordUserData;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonaService {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final PersonaRepository personaRepository;
    private final DiscordAuthService discordAuthService;

    private static final String PERSONA_ID_NOT_FOUND = "persona with id PERSONA_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonaService.class);

    public List<Persona> retrieveAllPersonas(final String userId) {

        LOGGER.debug("Entering retrieveAllPersonas. userId -> {}", userId);
        final List<Persona> personas = personaRepository.findAll()
                .stream()
                .filter(p -> hasReadPermissions(p, userId))
                .map(personaEntityToDTO)
                .toList();

        final Map<String, String> discordUsers = retrieveOwnerUsername(personas);
        return addOwnerToPersonas(personas, discordUsers);
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

    public PagedResponse<Persona> retrieveAllWithPagination(final String requesterDiscordId,
            final String searchCriteria, final String searchField, final int pageNumber, final int amountOfItems,
            final String sortBy) {

        Page<PersonaEntity> page;
        final String sortByField = StringUtils.isBlank(sortBy) ? "name" : sortBy;
        if (StringUtils.isBlank(searchField) || StringUtils.isBlank(searchCriteria)) {
            page = personaRepository.findAll(PageRequest.of(pageNumber - 1, amountOfItems, Sort.by(sortByField)));
            return buildPersonaPage(requesterDiscordId, page);
        }

        final AssetSpecification<PersonaEntity> spec = new AssetSpecification<>(searchField, searchCriteria);
        page = personaRepository.findAll(spec, PageRequest.of(pageNumber - 1, amountOfItems, Sort.by(sortByField)));

        return buildPersonaPage(requesterDiscordId, page);
    }

    private boolean hasReadPermissions(final PersonaEntity persona, final String userId) {

        final boolean isPublic = Visibility.isPublic(persona.getVisibility());
        final boolean isOwner = persona.getOwnerDiscordId()
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

        final boolean isOwner = persona.getOwnerDiscordId()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);
        return isOwner || canWrite;
    }

    private PagedResponse<Persona> buildPersonaPage(final String requesterDiscordId, Page<PersonaEntity> page) {

        final List<Persona> personas = page.getContent()
                .stream()
                .filter(p -> this.hasReadPermissions(p, requesterDiscordId))
                .map(personaEntityToDTO)
                .collect(Collectors.toList());

        final Map<String, String> discordUsers = retrieveOwnerUsername(personas);

        return PagedResponse.<Persona>builder()
                .currentPage(page.getNumber() + 1)
                .numberOfPages(page.getTotalPages())
                .data(addOwnerToPersonas(personas, discordUsers))
                .totalNumberOfItems((int) page.getTotalElements())
                .numberOfItemsInPage(page.getNumberOfElements())
                .build();
    }

    private Map<String, String> retrieveOwnerUsername(List<Persona> personas) {

        return personas.stream()
                .map(persona -> {
                    return persona.getOwnerDiscordId();
                })
                .collect(Collectors.toSet())
                .stream()
                .map(discordUserId -> {
                    return discordAuthService.retrieveDiscordUserById(discordUserId);
                })
                .collect(Collectors.toMap(DiscordUserData::getId, DiscordUserData::getUsername, (p1, p2) -> p1));
    }

    private List<Persona> addOwnerToPersonas(List<Persona> personas, Map<String, String> discordUsers) {

        return personas.stream()
                .map(persona -> {
                    discordUsers.entrySet()
                            .stream()
                            .filter(entry -> entry.getKey()
                                    .equals(persona.getOwnerDiscordId()))
                            .forEach(entry -> {
                                persona.setOwnerUsername(entry.getValue());
                            });

                    return persona;
                })
                .collect(Collectors.toList());
    }
}