package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lore/entry")
@CrossOrigin(origins = { "http://localhost:5173" })
public class LorebookEntryController {

    private final LorebookService lorebookService;

    private static final String RETRIEVE_ALL_LOREBOOKS_IN_LOREBOOK_REQUEST = "Received request for listing all lorebookEntries in loreboko with id {}";
    private static final String SAVE_LOREBOOK_ENTRY_REQUEST = "Received request for saving lorebookEntry -> {}. lorebookId -> {}";
    private static final String UPDATE_LOREBOOK_ENTRY_REQUEST = "Received request for updating lorebookEntry with ID {} -> {}";
    private static final String DELETE_LOREBOOK_ENTRY_REQUEST = "Received request for deleting lorebookEntry with ID {}";
    private static final String DELETE_LOREBOOK_ENTRY_RESPONSE = "Returning response for deleting lorebookEntry with ID {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookEntryController.class);

    @GetMapping("lorebook/{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> getAllLorebookEntriesFromLorebook(
            @RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId) {

        LOGGER.info(RETRIEVE_ALL_LOREBOOKS_IN_LOREBOOK_REQUEST, lorebookId);
        return Mono.just(lorebookService.retrieveAllLorebookEntriesInLorebook(lorebookId, requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all lorebookEntries from lorebook", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping("{lorebook-id}")
    public Mono<ResponseEntity<ApiResponse>> saveLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-id") final String lorebookId,
            @RequestBody final LorebookEntry lorebookEntry) {

        LOGGER.info(SAVE_LOREBOOK_ENTRY_REQUEST, lorebookEntry, lorebookId);
        return Mono.just(lorebookService.saveLorebookEntry(lorebookEntry, lorebookId, requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PutMapping("{lorebook-entry-id}")
    public Mono<ResponseEntity<ApiResponse>> updateLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-entry-id") final String lorebookEntryId,
            @RequestBody final LorebookEntry lorebookEntry) {

        LOGGER.info(UPDATE_LOREBOOK_ENTRY_REQUEST, lorebookEntryId, lorebookEntry);
        return Mono.just(lorebookService.updateLorebookEntry(lorebookEntryId, lorebookEntry, requesterUserId))
                .map(this::buildResponse)
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @DeleteMapping("{lorebook-entry-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteLorebookEntry(@RequestHeader("requester") String requesterUserId,
            @PathVariable(value = "lorebook-entry-id") final String lorebookEntryId) {

        LOGGER.info(DELETE_LOREBOOK_ENTRY_REQUEST, lorebookEntryId);
        return Mono.just(lorebookEntryId)
                .map(id -> {
                    lorebookService.deleteLorebookEntry(lorebookEntryId, requesterUserId);
                    LOGGER.info(DELETE_LOREBOOK_ENTRY_RESPONSE, lorebookEntryId);
                    return ResponseEntity.ok()
                            .body(ApiResponse.empty());
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ITEM_INSERTED_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ITEM_INSERTED_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(GENERAL_ERROR_MESSAGE, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    private ResponseEntity<ApiResponse> buildResponse(List<LorebookEntry> lorebookEntries) {

        LOGGER.info("Sending response for lorebookEntries -> {}", lorebookEntries);
        final ApiResponse respose = ApiResponse.builder()
                .lorebookEntries(lorebookEntries)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(LorebookEntry lorebookEntry) {

        LOGGER.info("Sending response for lorebookEntries -> {}", lorebookEntry);
        final ApiResponse respose = ApiResponse.builder()
                .lorebookEntry(lorebookEntry)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for lorebookEntries");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
