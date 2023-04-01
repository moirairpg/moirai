package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lore")
public class LorebookController {

    private final LorebookDTOToEntity lorebookDTOToEntity;
    private final LorebookEntityToDTO lorebookEntityToDTO;
    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;
    private final LorebookRepository lorebookRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;

    private static final String RETRIEVE_ALL_LOREBOOKS_REQUEST = "Received request for listing all lorebooks";
    private static final String RETRIEVE_ALL_LOREBOOKS_RESPONSE = "Returning response for listing all lorebooks request -> {}";
    private static final String RETRIEVE_LOREBOOK_BY_ID_REQUEST = "Received request for retrieving lorebook with id {}";
    private static final String RETRIEVE_LOREBOOK_BY_ID_RESPONSE = "Returning response for listing lorebook with id {} request -> {}";
    private static final String RETRIEVE_LOREBOOK_ENTRY_BY_ID_REQUEST = "Received request for retrieving lorebook entry with id {}";
    private static final String RETRIEVE_LOREBOOK_ENTRY_BY_ID_RESPONSE = "Returning response for listing lorebook entry with id {} request -> {}";
    private static final String SAVE_LOREBOOK_REQUEST = "Received request for saving lorebook -> {}";
    private static final String SAVE_LOREBOOK_RESPONSE = "Returning response for saving lorebook request -> {}";
    private static final String UPDATE_LOREBOOK_REQUEST = "Received request for updating lorebook with ID {} -> {}";
    private static final String UPDATE_LOREBOOK_RESPONSE = "Returning response for updating lorebook with id {} request -> {}";
    private static final String DELETE_LOREBOOK_REQUEST = "Received request for deleting lorebook with ID {}";
    private static final String DELETE_LOREBOOK_RESPONSE = "Returning response for deleting lorebook with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllLorebooks() {

        LOGGER.info(RETRIEVE_ALL_LOREBOOKS_REQUEST);
        return Mono.just(lorebookRepository.findAll())
                .map(l -> l.stream()
                        .map(lorebookEntityToDTO)
                        .collect(Collectors.toList()))
                .map(l -> ApiResponse.builder()
                        .lorebooks(l)
                        .build())
                .map(l -> {
                    LOGGER.info(RETRIEVE_ALL_LOREBOOKS_RESPONSE, l);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(l);
                });
    }

    @GetMapping("book/{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> getLorebookById(
            @PathVariable(value = "lorebook-id") final String lorebookId) {

        LOGGER.info(RETRIEVE_LOREBOOK_BY_ID_REQUEST, lorebookId);
        return Mono.just(lorebookRepository.findById(lorebookId))
                .map(l -> l.map(lorebookEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(l -> Stream.of(l)
                        .collect(Collectors.toList()))
                .map(l -> ApiResponse.builder()
                        .lorebooks(l)
                        .build())
                .map(l -> {
                    LOGGER.info(RETRIEVE_LOREBOOK_BY_ID_RESPONSE, lorebookId, l);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(l);
                });
    }

    @GetMapping("entry/{entry-id}")
    public Mono<ResponseEntity<ApiResponse>> getLorebookEntryById(
            @PathVariable(value = "entry-id") final String lorebookId) {

        LOGGER.info(RETRIEVE_LOREBOOK_ENTRY_BY_ID_REQUEST, lorebookId);
        return Mono.just(lorebookEntryRegexRepository.findById(lorebookId))
                .map(l -> l.map(lorebookEntryEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(l -> Stream.of(l)
                        .collect(Collectors.toList()))
                .map(l -> ApiResponse.builder()
                        .lorebookEntries(l)
                        .build())
                .map(l -> {
                    LOGGER.info(RETRIEVE_LOREBOOK_ENTRY_BY_ID_RESPONSE, lorebookId, l);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(l);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveLorebook(final Lorebook lorebook) {

        LOGGER.info(SAVE_LOREBOOK_REQUEST, lorebook);
        return Mono.just(lorebookDTOToEntity.apply(lorebook))
                .map(lorebookRepository::save)
                .map(l -> Stream.of(l)
                        .map(lorebookEntityToDTO)
                        .collect(Collectors.toList()))
                .map(l -> ApiResponse.builder()
                        .lorebooks(l)
                        .build())
                .map(l -> {
                    LOGGER.info(SAVE_LOREBOOK_RESPONSE, l);
                    return ResponseEntity.ok()
                            .body(l);
                });
    }

    @PatchMapping("{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> updateLorebookById(
            @PathVariable(value = "lorebook-id") final String lorebookId, final Lorebook lorebook) {

        LOGGER.info(UPDATE_LOREBOOK_REQUEST, lorebookId, lorebook);
        return Mono.just(lorebookDTOToEntity.apply(lorebook))
                .map(l -> {
                    l.setId(lorebookId);
                    return lorebookRepository.save(l);
                })
                .map(l -> Stream.of(l)
                        .map(lorebookEntityToDTO)
                        .collect(Collectors.toList()))
                .map(l -> ApiResponse.builder()
                        .lorebooks(l)
                        .build())
                .map(l -> {
                    LOGGER.info(UPDATE_LOREBOOK_RESPONSE, lorebookId, l);
                    return ResponseEntity.ok()
                            .body(l);
                });
    }

    @DeleteMapping("{lorebook-id}")
    public Mono<ResponseEntity<?>> deleteLorebookById(@PathVariable(value = "lorebook-id") final String lorebookId) {

        LOGGER.info(DELETE_LOREBOOK_REQUEST, lorebookId);
        return Mono.just(lorebookId)
                .map(id -> {
                    lorebookRepository.deleteById(id);
                    LOGGER.info(DELETE_LOREBOOK_RESPONSE, lorebookId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
