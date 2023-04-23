package es.thalesalv.chatrpg.application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
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

    private static final String DEFAULT_ID = "0";
    private static final String LOREBOOK_ID_NOT_FOUND = "lorebook with id LOREBOOK_ID could not be found in database.";
    private static final String LOREBOOK_ENTRY_ID_NOT_FOUND = "lorebook entry with id LOREBOOK_ENTRY_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookService.class);

    public List<Lorebook> retrieveAllLorebooks() {

        LOGGER.debug("Retrieving lorebook data from request");
        return lorebookRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(lorebookEntityToDTO)
                .toList();
    }

    public Lorebook retrieveLorebookById(final String lorebookId) {

        LOGGER.debug("Retrieving lorebook by ID data from request");
        return lorebookRepository.findById(lorebookId)
                .map(lorebookEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException(
                        "Error retrieving lorebook: " + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", lorebookId)));
    }

    public Lorebook saveLorebook(final Lorebook lorebook) {

        LOGGER.debug("Saving lorebook data from request");
        return Optional.of(lorebookDTOToEntity.apply(lorebook))
                .map(l -> {
                    l.getEntries()
                            .forEach(e -> {
                                e.setLorebook(l);
                                lorebookEntryRepository.save(e.getLorebookEntry());
                                lorebookEntryRegexRepository.save(e);
                            });

                    return l;
                })
                .map(lorebookRepository::save)
                .map(lorebookEntityToDTO)
                .orElseThrow(() -> new RuntimeException("There was a problem saving the new lorebook"));
    }

    public Lorebook updateLorebook(final String lorebookId, final Lorebook lorebook) {

        LOGGER.debug("Updating lorebook data from request. lorebookId -> {}", lorebookId);
        return Optional.of(lorebookDTOToEntity.apply(lorebook))
                .map(c -> {
                    c.setId(lorebookId);
                    return lorebookRepository.save(c);
                })
                .map(lorebookEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error updating lorebook with id " + lorebookId));
    }

    public void deleteLorebook(final String lorebookId) {

        LOGGER.debug("Deleting lorebook data from request");
        lorebookRepository.findById(lorebookId)
                .map(lorebook -> {
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
                    return lorebook;
                })
                .orElseThrow(() -> new LorebookNotFoundException(
                        "Error deleting lorebook: " + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", lorebookId)));
    }

    public List<LorebookEntry> retrieveAllLorebookEntries() {

        LOGGER.debug("Retrieving lorebookEntry data from request");
        return lorebookEntryRegexRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(lorebookEntryEntityToDTO)
                .toList();
    }

    public List<LorebookEntry> retrieveAllLorebookEntriesInLorebook(final String lorebookId) {

        LOGGER.debug("Retrieving lorebookEntry data from lorebook with id {} from request", lorebookId);
        return lorebookEntryRegexRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .filter(l -> l.getLorebook()
                        .getId()
                        .equals(lorebookId))
                .map(lorebookEntryEntityToDTO)
                .toList();
    }

    public LorebookEntry retrieveLorebookEntryById(final String lorebookEntryId) {

        LOGGER.debug("Retrieving lorebookEntry by ID data from request");
        return lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    return lorebookEntryRegexRepository.findByLorebookEntry(entry)
                            .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry regex not found"));
                })
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error retrieving lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));
    }

    public LorebookEntry saveLorebookEntry(final LorebookEntry lorebookEntry, final String lorebookId) {

        LOGGER.debug("Saving lorebookEntry data from request");
        return lorebookRepository.findById(lorebookId)
                .map(lorebook -> {
                    final LorebookEntryRegexEntity regexEntity = lorebookEntryDTOToEntity.apply(lorebookEntry);
                    regexEntity.setLorebook(lorebook);
                    lorebookEntryRepository.save(regexEntity.getLorebookEntry());
                    return lorebookEntryRegexRepository.save(regexEntity);
                })
                .map(lorebookEntryEntityToDTO)
                .orElseThrow(() -> new LorebookNotFoundException("Error saving lorebook entry to lorebook: "
                        + LOREBOOK_ID_NOT_FOUND.replace("LOREBOOK_ID", lorebookId)));
    }

    public LorebookEntry updateLorebookEntry(final String lorebookEntryId, final LorebookEntry lorebookEntry) {

        LOGGER.debug("Updating lorebookEntry data from request");
        lorebookEntry.setId(lorebookEntryId);
        return Optional.of(lorebookEntryDTOToEntity.apply(lorebookEntry))
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

    public void deleteLorebookEntry(final String lorebookEntryId) {

        LOGGER.debug("Deleting lorebookEntry data from request");
        lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    lorebookEntryRegexRepository.deleteByLorebookEntry(entry);
                    lorebookEntryRepository.delete(entry);
                    return entry;
                })
                .orElseThrow(() -> new LorebookNotFoundException("Error deleting lorebook entry: "
                        + LOREBOOK_ENTRY_ID_NOT_FOUND.replace("LOREBOOK_ENTRY_ID", lorebookEntryId)));
    }
}
