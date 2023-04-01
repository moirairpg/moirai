package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/lore")
public class LorebookController {

    private final LorebookEntityToDTO lorebookEntityToDTO;
    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;

    private final LorebookRepository lorebookRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookController.class);

    @GetMapping
    public Mono<List<Lorebook>> getAllLorebooks() {

        LOGGER.debug("Received request for listing all lorebooks");
        return Mono.just(lorebookRepository.findAll())
                .map(lorebooks -> lorebooks.stream()
                        .map(lorebookEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("book/{lorebook-id}")
    public Mono<Lorebook> getLorebookById(@PathVariable(value = "lorebook-id") final String lorebooklId) {

        LOGGER.debug("Received request for retrieving lorebook with id {}", lorebooklId);
        return Mono.just(lorebookRepository.findById(lorebooklId))
                .map(lorebook -> lorebook.map(lorebookEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()));
    }

    @GetMapping("entry/{entry-id}")
    public Mono<LorebookEntry> getLorebookEntryById(@PathVariable(value = "entry-id") final String lorebooklId) {

        LOGGER.debug("Received request for retrieving lorebook entry with id {}", lorebooklId);
        return Mono.just(lorebookEntryRegexRepository.findById(lorebooklId))
                .map(lorebook -> lorebook.map(lorebookEntryEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()));
    }
}
