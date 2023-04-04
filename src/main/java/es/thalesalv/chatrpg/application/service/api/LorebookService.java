package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
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

    public Mono<List<Lorebook>> retrieveAllLorebooks() {

        LOGGER.debug("Retrieving lorebook data from request");
        return Mono.just(lorebookRepository.findAll())
                .map(lorebooks -> lorebooks.stream()
                        .map(lorebookEntityToDTO)
                        .toList());
    }

    public Mono<List<Lorebook>> retrieveLorebookById(final String lorebookId) {

        LOGGER.debug("Retrieving lorebook by ID data from request");
        return Mono.just(lorebookRepository.findById(lorebookId)
                .orElseThrow(LorebookNotFoundException::new))
                .map(lorebookEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<Lorebook>> saveLorebook(final Lorebook lorebook) {

        LOGGER.debug("Saving lorebook data from request");
        return Mono.just(lorebookDTOToEntity.apply(lorebook))
                .map(lorebookRepository::save)
                .map(lorebookEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<Lorebook>> updateLorebook(final String lorebookId, final Lorebook lorebook) {

        LOGGER.debug("Updating lorebook data from request. lorebookId -> {}", lorebookId);
        return Mono.just(lorebookDTOToEntity.apply(lorebook))
                .map(c -> {
                    c.setId(lorebookId);
                    return lorebookRepository.save(c);
                })
                .map(lorebookEntityToDTO)
                .map(Arrays::asList);
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

    public Mono<List<LorebookEntry>> retrieveAllLorebookEntries() {

        LOGGER.debug("Retrieving lorebookEntry data from request");
        return Mono.just(lorebookEntryRegexRepository.findAll())
                .map(lorebookEntries -> lorebookEntries.stream()
                        .map(lorebookEntryEntityToDTO)
                        .toList());
    }

    public Mono<List<LorebookEntry>> retrieveLorebookEntryById(final String lorebookEntryId) {

        LOGGER.debug("Retrieving lorebookEntry by ID data from request");
        return Mono.just(lorebookEntryRepository.findById(lorebookEntryId)
                .map(entry -> {
                    return lorebookEntryRegexRepository.findByLorebookEntry(entry)
                            .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry regex not found"));
                })
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(() -> new LorebookEntryNotFoundException("Lorebook entry not found")));
    }

    public Mono<List<LorebookEntry>> saveLorebookEntry(final LorebookEntry lorebookEntry) {

        LOGGER.debug("Saving lorebookEntry data from request");
        return Mono.just(lorebookEntryDTOToEntity.apply(lorebookEntry))
                .map(c -> {
                    lorebookEntryRepository.save(c.getLorebookEntry());
                    return lorebookEntryRegexRepository.save(c);
                })
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList);
    }

    public Mono<List<LorebookEntry>> updateLorebookEntry(final String lorebookEntryId,
            final LorebookEntry lorebookEntry) {

        LOGGER.debug("Updating lorebookEntry data from request");
        lorebookEntry.setId(lorebookEntryId);
        return Mono.just(lorebookEntryDTOToEntity.apply(lorebookEntry))
                .map(c -> lorebookEntryRegexRepository.findByLorebookEntry(c.getLorebookEntry())
                        .map(regexEntry -> {
                            regexEntry.setRegex(c.getRegex());
                            lorebookEntryRepository.save(regexEntry.getLorebookEntry());
                            return lorebookEntryRegexRepository.save(regexEntry);
                        })
                        .orElseThrow(LorebookEntryNotFoundException::new))
                .map(lorebookEntryEntityToDTO)
                .map(Arrays::asList);
    }

    public void deleteLorebookEntry(final String lorebookEntryId) {

        LOGGER.debug("Deleting lorebookEntry data from request");
        lorebookEntryRepository.findById(lorebookEntryId)
                .ifPresentOrElse(lorebookEntryRegexRepository::deleteByLorebookEntry, () -> {
                    throw new LorebookEntryNotFoundException("Could not find requested lore entry");
                });
    }
}
