package es.thalesalv.chatrpg.application.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.enums.Visibility;
import es.thalesalv.chatrpg.domain.exception.InsufficientPermissionException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.LorebookNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class LorebookService {

    private final LorebookDTOToEntity lorebookDTOToEntity;
    private final LorebookEntityToDTO lorebookEntityToDTO;
    private final LorebookEntryDTOToEntity lorebookEntryDTOToEntity;
    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;

    private final WorldRepository worldRepository;
    private final LorebookRepository lorebookRepository;
    private final LorebookEntryRepository lorebookEntryRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;

    private static final String LOREBOOK_ID_NOT_FOUND = "lorebook with id LOREBOOK_ID could not be found in database.";
    private static final String LOREBOOK_ENTRY_ID_NOT_FOUND = "lorebook entry with id LOREBOOK_ENTRY_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookService.class);

    public List<Lorebook> retrieveAllLorebooks(final String userId) {

        LOGGER.debug("Entering retrieveAllLorebooks. userId -> {}", userId);
        return lorebookRepository.findAll()
                .stream()
                .filter(l -> hasReadPermissions(l, userId))
                .map(lorebookEntityToDTO)
                .toList();
    }

    public Lorebook saveLorebook(final Lorebook lorebook) {

        LOGGER.debug("Entering saveLorebook. lorebook -> {}", lorebook);
        final LorebookEntity convertedEntity = lorebookDTOToEntity.apply(lorebook);
        convertedEntity.getEntries()
                .forEach(entry -> {
                    lorebookEntryRepository.save(entry.getLorebookEntry());
                    lorebookEntryRegexRepository.save(entry);
                });

        final LorebookEntity updatedEntity = lorebookRepository.save(convertedEntity);
        return lorebookEntityToDTO.apply(updatedEntity);
    }

    public Lorebook updateLorebook(final String lorebookId, final Lorebook lorebook, final String userId) {

        LOGGER.debug("Entering updateLorebook. lorebookId -> {}, userId -> {}, lorebook -> {}", lorebookId, userId,
                lorebook);

        lorebookRepository.findById(lorebookId)
                .orElseThrow(
                        () -> new LorebookNotFoundException("The requested lorebook for update could not be found"));

        final LorebookEntity lorebookEntity = lorebookDTOToEntity.apply(lorebook);
        if (!hasWritePermissions(lorebookEntity, userId)) {
            throw new InsufficientPermissionException("Not enough permissions to modify this lorebook");
        }

        lorebookEntity.setId(lorebookId);
        final LorebookEntity updatedEntity = lorebookRepository.save(lorebookEntity);
        return lorebookEntityToDTO.apply(updatedEntity);
    }

    public void deleteLorebook(final String lorebookId, final String userId) {

        LOGGER.debug("Entering deleteLorebook. lorebookId -> {}, userId -> {}", lorebookId, userId);
        lorebookRepository.findById(lorebookId)
                .ifPresentOrElse(lorebook -> {
                    if (!hasWritePermissions(lorebook, userId)) {
                        throw new InsufficientPermissionException("Not enough permissions to delete this lorebook");
                    }

                    lorebook.getEntries()
                            .forEach(entry -> {
                                lorebookEntryRegexRepository.delete(entry);
                                lorebookEntryRepository.delete(entry.getLorebookEntry());
                            });

                    worldRepository.findByLorebook(lorebook)
                            .forEach(world -> {
                                final LorebookEntity defaultLorebook = lorebookDTOToEntity
                                        .apply(Lorebook.defaultLorebook());

                                world.setLorebook(defaultLorebook);
                                worldRepository.save(world);
                            });

                    lorebookRepository.delete(lorebook);
                }, () -> {
                    throw new LorebookNotFoundException(
                            "Error deleting lorebook: " + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", lorebookId));
                });
    }

    public List<LorebookEntry> retrieveAllLorebookEntriesInLorebook(final String lorebookId, final String userId) {

        LOGGER.debug("Entering retrieveAllLorebookEntriesInLorebook. lorebookId -> {}, userId -> {}", lorebookId,
                userId);

        return lorebookRepository.findById(lorebookId)
                .map(l -> {
                    if (!hasReadPermissions(l, userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to retrieve entries in this lorebook");
                    }

                    return l.getEntries();
                })
                .map(es -> es.stream()
                        .map(lorebookEntryEntityToDTO::apply)
                        .toList())
                .orElseThrow(() -> new LorebookNotFoundException("The lorebook requested could not be found"));
    }

    public LorebookEntry retrieveLorebookEntryById(final String lorebookEntryId, final String userId) {

        LOGGER.debug("Entering retrieveLorebookEntryById. lorebookEntryId -> {}. userId -> {}", lorebookEntryId,
                userId);
        return lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    return lorebookEntryRegexRepository.findByLorebookEntry(entry)
                            .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry regex not found"));
                })
                .map(e -> {
                    if (!hasReadPermissions(e.getLorebook(), userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to retrieve entries in this lorebook");
                    }

                    return e;
                })
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error retrieving lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));
    }

    public LorebookEntry saveLorebookEntry(final LorebookEntry lorebookEntry, final String lorebookId,
            final String userId) {

        LOGGER.debug("Entering saveLorebookEntry. lorebookId -> {}, userId -> {}, lorebookEntry -> {}", lorebookId,
                userId, lorebookEntry);

        return lorebookRepository.findById(lorebookId)
                .map(lorebook -> {
                    if (!hasWritePermissions(lorebook, userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to add entries to this lorebook");
                    }

                    final LorebookEntryRegexEntity regexEntity = lorebookEntryDTOToEntity.apply(lorebookEntry);
                    regexEntity.setLorebook(lorebook);
                    lorebookEntryRepository.save(regexEntity.getLorebookEntry());
                    return lorebookEntryRegexRepository.save(regexEntity);
                })
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error saving lorebook entry to lorebook: "
                        + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", lorebookId)));
    }

    public LorebookEntry updateLorebookEntry(final String lorebookEntryId, final LorebookEntry lorebookEntry,
            final String userId) {

        LOGGER.debug("Entering updateLorebookEntry. lorebookEntryId -> {}, userId -> {}, lorebookEntry -> {}",
                lorebookEntryId, userId, lorebookEntry);

        final LorebookEntryEntity lorebookEntryEntity = lorebookEntryRepository.findById(lorebookEntryId)
                .orElseThrow(() -> new LorebookEntryNotFoundException("Error updating lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));

        final LorebookEntryRegexEntity lorebookRegex = lorebookEntryRegexRepository
                .findByLorebookEntry(lorebookEntryEntity)
                .orElseThrow(() -> new LorebookEntryNotFoundException(
                        "The requested entry does not have a regex entry attached to it"));

        if (!hasWritePermissions(lorebookRegex.getLorebook(), userId)) {
            throw new InsufficientPermissionException("Not enough permissions to modify entries in this lorebook");
        }

        lorebookEntryEntity.setName(lorebookEntry.getName());
        lorebookEntryEntity.setDescription(lorebookEntry.getDescription());
        lorebookEntryEntity.setPlayerDiscordId(lorebookEntry.getPlayerDiscordId());
        lorebookRegex.setRegex(lorebookEntry.getRegex());
        lorebookRegex.setLorebookEntry(lorebookEntryEntity);

        lorebookEntryRepository.save(lorebookEntryEntity);
        lorebookEntryRegexRepository.save(lorebookRegex);
        return lorebookEntryEntityToDTO.apply(lorebookRegex);
    }

    public void deleteLorebookEntry(final String lorebookEntryId, final String userId) {

        LOGGER.debug("Entering deleteLorebookEntry. lorebookEntryId -> {}, userId -> {}", lorebookEntryId, userId);
        lorebookEntryRepository.findById(lorebookEntryId)
                .ifPresentOrElse(entry -> {
                    lorebookEntryRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder()
                            .id(lorebookEntryId)
                            .build())
                            .ifPresentOrElse(e -> {
                                if (!hasWritePermissions(e.getLorebook(), userId)) {
                                    throw new InsufficientPermissionException(
                                            "Not enough permissions to delete entries in this lorebook");
                                }
                            }, () -> {
                                throw new LorebookEntryNotFoundException(
                                        "The requested entry does not have a regex entry attached to it");
                            });

                    lorebookEntryRegexRepository.deleteByLorebookEntry(entry);
                    lorebookEntryRepository.delete(entry);
                }, () -> {
                    throw new LorebookEntryNotFoundException("Error deleting entry: "
                            + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId));
                });
    }

    private boolean hasReadPermissions(final LorebookEntity lorebook, final String userId) {

        final List<String> readPermissions = Optional.ofNullable(lorebook.getReadPermissions())
                .orElse(Collections.emptyList());

        final List<String> writePermissions = Optional.ofNullable(lorebook.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isPublic = Visibility.isPublic(lorebook.getVisibility());
        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canRead = readPermissions.contains(userId) || writePermissions.contains(userId);

        return isPublic || (isOwner || canRead);
    }

    private boolean hasWritePermissions(final LorebookEntity lorebook, final String userId) {

        final List<String> writePermissions = Optional.ofNullable(lorebook.getWritePermissions())
                .orElse(Collections.emptyList());

        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canWrite = writePermissions.contains(userId);

        return isOwner || canWrite;
    }
}