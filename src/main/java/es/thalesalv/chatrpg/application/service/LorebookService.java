package es.thalesalv.chatrpg.application.service;

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
                        .map(e -> {
                            return lorebookEntryEntityToDTO.apply(e);
                        })
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

        lorebookEntry.setId(lorebookEntryId);
        return Optional.of(lorebookEntryDTOToEntity.apply(lorebookEntry))
                .map(c -> {
                    if (!hasWritePermissions(c.getLorebook(), userId)) {
                        throw new InsufficientPermissionException(
                                "Not enough permissions to modify entries in this lorebook");
                    }

                    return c;
                })
                .map(c -> lorebookEntryRegexRepository.findByLorebookEntry(c.getLorebookEntry())
                        .map(regexEntry -> {
                            c.setId(regexEntry.getId());
                            c.setLorebook(regexEntry.getLorebook());
                            lorebookEntryRepository.save(c.getLorebookEntry());
                            return lorebookEntryRegexRepository.save(c);
                        })
                        .orElseThrow(LorebookEntryNotFoundException::new))
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error updating lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));
    }

    public void deleteLorebookEntry(final String lorebookEntryId, final String userId) {

        LOGGER.debug("Entering deleteLorebookEntry. lorebookEntryId -> {}, userId -> {}", lorebookEntryId, userId);
        lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    lorebookEntryRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder()
                            .id(lorebookEntryId)
                            .build())
                            .ifPresent(e -> {
                                if (!hasWritePermissions(e.getLorebook(), userId)) {
                                    throw new InsufficientPermissionException(
                                            "Not enough permissions to delete entries in this lorebook");
                                }
                            });

                    lorebookEntryRegexRepository.deleteByLorebookEntry(entry);
                    lorebookEntryRepository.delete(entry);
                    return entry;
                })
                .orElseThrow(() -> new LorebookNotFoundException("Error deleting lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));
    }

    private boolean hasReadPermissions(final LorebookEntity lorebook, final String userId) {

        final boolean isPublic = Visibility.isPublic(lorebook.getVisibility());
        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canRead = lorebook.getReadPermissions()
                .contains(userId)
                || lorebook.getWritePermissions()
                        .contains(userId);

        return isPublic || (isOwner || canRead);
    }

    private boolean hasWritePermissions(final LorebookEntity lorebook, final String userId) {

        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canWrite = lorebook.getWritePermissions()
                .contains(userId);

        return isOwner || canWrite;
    }
}
