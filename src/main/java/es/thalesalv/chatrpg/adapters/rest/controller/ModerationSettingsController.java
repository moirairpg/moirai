package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import es.thalesalv.chatrpg.application.service.api.ModerationSettingsService;
import es.thalesalv.chatrpg.domain.exception.ModerationSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiError;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/moderation-settings")
public class ModerationSettingsController {

    private final ModerationSettingsService moderationSettingsService;

    private static final String RETRIEVE_ALL_MODERATION_SETTINGS_REQUEST = "Received request for listing all moderation settings";
    private static final String RETRIEVE_MODERATION_SETTINGS_BY_ID_REQUEST = "Received request for retrieving moderation settings with id {}";
    private static final String SAVE_MODERATION_SETTINGS_REQUEST = "Received request for saving moderation settings -> {}";
    private static final String UPDATE_MODERATION_SETTINGS_REQUEST = "Received request for updating moderation settings with ID {} -> {}";
    private static final String DELETE_MODERATION_SETTINGS_REQUEST = "Received request for deleting moderation settings with ID {}";
    private static final String DELETE_MODERATION_SETTINGS_RESPONSE = "Returning response for deleting moderation settings with ID {}";
    private static final String ID_CANNOT_BE_NULL = "moderation settings ID cannot be null";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving moderation settings with id {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested moderation settings was not found";
    private static final String MODERATION_SETTINGS_WITH_ID_NOT_FOUND = "Couldn't find requested moderation settings with ID {}";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllModerationSettings() {

        LOGGER.info(RETRIEVE_ALL_MODERATION_SETTINGS_REQUEST);
        return moderationSettingsService.retrieveAllModerationSettings()
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all moderation settings", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @GetMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> getModerationSettingsById(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId) {

        LOGGER.info(RETRIEVE_MODERATION_SETTINGS_BY_ID_REQUEST, moderationSettingsId);
        return moderationSettingsService.retrieveModerationSettingsById(moderationSettingsId)
                .map(this::buildResponse)
                .onErrorResume(ModerationSettingsNotFoundException.class, e -> {
                    LOGGER.error(MODERATION_SETTINGS_WITH_ID_NOT_FOUND, moderationSettingsId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.NOT_FOUND, REQUESTED_NOT_FOUND)));
                })
                .onErrorResume(IllegalArgumentException.class, e -> {
                    LOGGER.error(ID_CANNOT_BE_NULL, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.BAD_REQUEST, ID_CANNOT_BE_NULL)));
                })
                .onErrorResume(e -> {
                    LOGGER.error(ERROR_RETRIEVING_WITH_ID, moderationSettingsId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveModerationSettings(final ModerationSettings moderationSettings) {

        LOGGER.info(SAVE_MODERATION_SETTINGS_REQUEST, moderationSettings);
        return moderationSettingsService.saveModerationSettings(moderationSettings)
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

    @PatchMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> updateModerationSettings(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId,
            final ModerationSettings moderationSettings) {

        LOGGER.info(UPDATE_MODERATION_SETTINGS_REQUEST, moderationSettingsId, moderationSettings);
        return moderationSettingsService.updateModerationSettings(moderationSettingsId, moderationSettings)
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

    @DeleteMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteModerationSettings(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId) {

        LOGGER.info(DELETE_MODERATION_SETTINGS_REQUEST, moderationSettingsId);
        return Mono.just(moderationSettingsId)
                .map(id -> {
                    moderationSettingsService.deleteModerationSettings(moderationSettingsId);
                    LOGGER.info(DELETE_MODERATION_SETTINGS_RESPONSE, moderationSettingsId);
                    return buildResponse(null);
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

    private ResponseEntity<ApiResponse> buildResponse(List<ModerationSettings> moderationSettings) {

        LOGGER.info("Sending response for moderation settings -> {}", moderationSettings);
        final ApiResponse respose = ApiResponse.builder()
                .moderationSettings(moderationSettings)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for moderation settings");
        return ApiResponse.builder()
                .error(ApiError.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
