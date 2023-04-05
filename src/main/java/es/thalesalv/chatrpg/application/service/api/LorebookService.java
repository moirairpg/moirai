package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookService.class);

    public List<Lorebook> retrieveAllLorebooks() {

        LOGGER.debug("Retrieving lorebook data from request");
        return lorebookRepository.findAll()
                .stream()
                .map(lorebookEntityToDTO)
                .toList();
    }

    public List<Lorebook> retrieveLorebookById(final String lorebookId) {

        LOGGER.debug("Retrieving lorebook by ID data from request");
        return lorebookRepository.findById(lorebookId)
                .map(lorebookEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(LorebookNotFoundException::new);
    }

    public List<Lorebook> saveLorebook(final Lorebook lorebook) {

        LOGGER.debug("Saving lorebook data from request");
        return Optional.of(lorebookDTOToEntity.apply(lorebook))
                .map(lorebookRepository::save)
                .map(lorebookEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new RuntimeException("There was a problem saving the new lorebook"));
    }

    public List<Lorebook> updateLorebook(final String lorebookId, final Lorebook lorebook) {

        LOGGER.debug("Updating lorebook data from request. lorebookId -> {}", lorebookId);
        return Optional.of(lorebookDTOToEntity.apply(lorebook))
                .map(c -> {
                    c.setId(lorebookId);
                    return lorebookRepository.save(c);
                })
                .map(lorebookEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new RuntimeException("There was a problem updating the new lorebook"));
    }

    public void deleteLorebook(final String lorebookId) {

        LOGGER.debug("Deleting lorebook data from request");
        lorebookRepository.findById(lorebookId)
                .ifPresentOrElse(lorebook -> {
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
                    throw new LorebookEntryNotFoundException("Could not find requested lorebook");
                });
    }

    public List<LorebookEntry> retrieveAllLorebookEntries() {

        LOGGER.debug("Retrieving lorebookEntry data from request");
        return lorebookEntryRegexRepository.findAll()
                .stream()
                .map(lorebookEntryEntityToDTO)
                .toList();
    }

    public List<LorebookEntry> retrieveLorebookEntryById(final String lorebookEntryId) {

        LOGGER.debug("Retrieving lorebookEntry by ID data from request");
        return lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    return lorebookEntryRegexRepository.findByLorebookEntry(entry)
                            .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry regex not found"));
                })
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry not found"));
    }

    public List<LorebookEntry> saveLorebookEntry(final LorebookEntry lorebookEntry, final String lorebookId) {

        LOGGER.debug("Saving lorebookEntry data from request");
        return lorebookRepository.findById(lorebookId)
                .map(lorebook -> {
                    final LorebookEntryRegexEntity regexEntity = lorebookEntryDTOToEntity.apply(lorebookEntry);
                    regexEntity.setLorebook(lorebook);
                    lorebookEntryRepository.save(regexEntity.getLorebookEntry());
                    return lorebookEntryRegexRepository.save(regexEntity);
                })
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(
                        () -> new LorebookNotFoundException("The lorebook request to add this entry to was not found"));
    }

    public List<LorebookEntry> updateLorebookEntry(final String lorebookEntryId, final LorebookEntry lorebookEntry) {

        LOGGER.debug("Updating lorebookEntry data from request");
        lorebookEntry.setId(lorebookEntryId);
        return Optional.of(lorebookEntryDTOToEntity.apply(lorebookEntry))
                .map(c -> lorebookEntryRegexRepository.findByLorebookEntry(c.getLorebookEntry())
                        .map(regexEntry -> {
                            regexEntry.setRegex(c.getRegex());
                            lorebookEntryRepository.save(regexEntry.getLorebookEntry());
                            return lorebookEntryRegexRepository.save(regexEntry);
                        })
                        .orElseThrow(LorebookEntryNotFoundException::new))
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(LorebookEntryNotFoundException::new);
    }

    public void deleteLorebookEntry(final String lorebookEntryId) {

        LOGGER.debug("Deleting lorebookEntry data from request");
        lorebookEntryRepository.findById(lorebookEntryId)
                .ifPresentOrElse(entry -> {
                    lorebookEntryRegexRepository.deleteByLorebookEntry(entry);
                    lorebookEntryRepository.delete(entry);
                }, () -> {
                    throw new LorebookEntryNotFoundException("Could not find requested lore entry");
                });
    }
}
