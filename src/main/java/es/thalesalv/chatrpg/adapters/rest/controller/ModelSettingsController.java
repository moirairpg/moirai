package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
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
@RequestMapping("/settings")
public class ModelSettingsController {

    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModelSettingsEntityToDTO modelSettingsEntityToDTO;
    private final ModelSettingsRepository modelSettingsRepository;

    private static final String RETRIEVE_ALL_SETTINGS_REQUEST = "Received request for listing all model settings";
    private static final String RETRIEVE_ALL_SETTINGS_RESPONSE = "Returning response for listing all model settings request -> {}";
    private static final String RETRIEVE_SETTINGS_BY_ID_REQUEST = "Received request for retrieving model settings with id {}";
    private static final String RETRIEVE_SETTINGS_BY_ID_RESPONSE = "Returning response for listing model settings with id {} request -> {}";
    private static final String SAVE_SETTINGS_REQUEST = "Received request for saving model settings -> {}";
    private static final String SAVE_SETTINGS_RESPONSE = "Returning response for saving model settings request -> {}";
    private static final String UPDATE_SETTINGS_REQUEST = "Received request for updating model settings with ID {} -> {}";
    private static final String UPDATE_SETTINGS_RESPONSE = "Returning response for updating model settings with id {} request -> {}";
    private static final String DELETE_SETTINGS_REQUEST = "Received request for deleting model settings with ID {}";
    private static final String DELETE_SETTINGS_RESPONSE = "Returning response for deleting model settings with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSettingsController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllSettings() {

        LOGGER.info(RETRIEVE_ALL_SETTINGS_REQUEST);
        return Mono.just(modelSettingsRepository.findAll())
                .map(p -> p.stream()
                        .map(modelSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .modelSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_ALL_SETTINGS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @GetMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> getModelSettingsById(
            @PathVariable(value = "model-settings-id") final String modelSettingsId) {

        LOGGER.info(RETRIEVE_SETTINGS_BY_ID_REQUEST, modelSettingsId);
        return Mono.just(modelSettingsRepository.findById(modelSettingsId))
                .map(p -> p.map(modelSettingsEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(p -> Stream.of(p)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .modelSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_SETTINGS_BY_ID_RESPONSE, modelSettingsId, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveModelSettings(final ModelSettings modelSettings) {

        LOGGER.info(SAVE_SETTINGS_REQUEST, modelSettings);
        return Mono.just(modelSettingsDTOToEntity.apply(modelSettings))
                .map(modelSettingsRepository::save)
                .map(p -> Stream.of(p)
                        .map(modelSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .modelSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(SAVE_SETTINGS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @PatchMapping("{model-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> updateModelSettingsById(
            @PathVariable(value = "model-settings-id") final String modelSettingsId,
            final ModelSettings modelSettings) {

        LOGGER.info(UPDATE_SETTINGS_REQUEST, modelSettingsId, modelSettings);
        return Mono.just(modelSettingsDTOToEntity.apply(modelSettings))
                .map(p -> {
                    p.setId(modelSettingsId);
                    return modelSettingsRepository.save(p);
                })
                .map(p -> Stream.of(p)
                        .map(modelSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .modelSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(UPDATE_SETTINGS_RESPONSE, modelSettingsId, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @DeleteMapping("{model-settings-id}")
    public Mono<ResponseEntity<?>> deleteModelSettingsById(
            @PathVariable(value = "model-settings-id") final String modelSettingsId) {

        LOGGER.info(DELETE_SETTINGS_REQUEST, modelSettingsId);
        return Mono.just(modelSettingsId)
                .map(id -> {
                    modelSettingsRepository.deleteById(id);
                    LOGGER.info(DELETE_SETTINGS_RESPONSE, modelSettingsId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
