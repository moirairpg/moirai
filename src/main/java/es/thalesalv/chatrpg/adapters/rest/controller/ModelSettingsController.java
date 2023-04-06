package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;

import es.thalesalv.chatrpg.application.service.api.ModelSettingsService;
import es.thalesalv.chatrpg.domain.exception.ModelSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.api.ApiErrorResponse;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/settings/model")
public class ModelSettingsController {

    private final ModelSettingsService modelSettingsService;

    private static final String RETRIEVE_ALL_MODEL_SETTINGS_REQUEST = "Received request for listing all model settings";
    private static final String RETRIEVE_MODEL_SETTINGS_BY_ID_REQUEST = "Received request for retrieving model settings with id {}";
    private static final String SAVE_MODEL_SETTINGS_REQUEST = "Received request for saving model settings -> {}";
    private static final String UPDATE_MODEL_SETTINGS_REQUEST = "Received request for updating model settings with ID {} -> {}";
    private static final String DELETE_MODEL_SETTINGS_REQUEST = "Received request for deleting model settings with ID {}";
    private static final String DELETE_MODEL_SETTINGS_RESPONSE = "Returning response for deleting model settings with ID {}";
    private static final String ID_CANNOT_BE_NULL = "model settings ID cannot be null";
    private static final String ERROR_RETRIEVING_WITH_ID = "Error retrieving model settings with id {}";
    private static final String GENERAL_ERROR_MESSAGE = "An error occurred processing the request";
    private static final String REQUESTED_NOT_FOUND = "The requested model settings was not found";
    private static final String MODEL_SETTINGS_WITH_ID_NOT_FOUND = "Couldn't find requested model settings with ID {}";
    private static final String ITEM_INSERTED_CANNOT_BE_NULL = "The item to be inserted cannot be null";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSettingsController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllModelSettings() {

        LOGGER.info(RETRIEVE_ALL_MODEL_SETTINGS_REQUEST);
        return Mono.just(modelSettingsService.retrieveAllModelSettings())
                .map(this::buildResponse)
                .onErrorResume(e -> {
                    LOGGER.error("Error retrieving all model settings", e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @GetMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> getModelSettingsById(
            @PathVariable(value = "model-settings-id") final String modelSettingsId) {

        LOGGER.info(RETRIEVE_MODEL_SETTINGS_BY_ID_REQUEST, modelSettingsId);
        return Mono.just(modelSettingsService.retrieveModelSettingsById(modelSettingsId))
                .map(this::buildResponse)
                .onErrorResume(ModelSettingsNotFoundException.class, e -> {
                    LOGGER.error(MODEL_SETTINGS_WITH_ID_NOT_FOUND, modelSettingsId, e);
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
                    LOGGER.error(ERROR_RETRIEVING_WITH_ID, modelSettingsId, e);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, GENERAL_ERROR_MESSAGE)));
                });
    }

    @PostMapping
    public Mono<ResponseEntity<ApiResponse>> saveModelSettings(@RequestBody final ModelSettings modelSettings) {

        LOGGER.info(SAVE_MODEL_SETTINGS_REQUEST, modelSettings);
        return Mono.just(modelSettingsService.saveModelSettings(modelSettings))
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

    @PutMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> updateModelSettings(
            @PathVariable(value = "model-settings-id") final String modelSettingsId,
            @RequestBody final ModelSettings modelSettings) {

        LOGGER.info(UPDATE_MODEL_SETTINGS_REQUEST, modelSettingsId, modelSettings);
        return Mono.just(modelSettingsService.updateModelSettings(modelSettingsId, modelSettings))
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

    @DeleteMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> deleteModelSettings(
            @PathVariable(value = "model-settings-id") final String modelSettingsId) {

        LOGGER.info(DELETE_MODEL_SETTINGS_REQUEST, modelSettingsId);
        return Mono.just(modelSettingsId)
                .map(id -> {
                    modelSettingsService.deleteModelSettings(modelSettingsId);
                    LOGGER.info(DELETE_MODEL_SETTINGS_RESPONSE, modelSettingsId);
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

    private ResponseEntity<ApiResponse> buildResponse(List<ModelSettings> modelSettings) {

        LOGGER.info("Sending response for model settings -> {}", modelSettings);
        final ApiResponse respose = ApiResponse.builder()
                .modelSettingsList(modelSettings)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ResponseEntity<ApiResponse> buildResponse(ModelSettings modelSettings) {

        LOGGER.info("Sending response for model settings -> {}", modelSettings);
        final ApiResponse respose = ApiResponse.builder()
                .modelSettings(modelSettings)
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(respose);
    }

    private ApiResponse buildErrorResponse(HttpStatus status, String message) {

        LOGGER.debug("Building error response object for model settings");
        return ApiResponse.builder()
                .error(ApiErrorResponse.builder()
                        .message(message)
                        .status(status)
                        .build())
                .build();
    }
}
