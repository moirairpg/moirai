package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.model.api.ApiResponse;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
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
public class ModerationSettingsController {

    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityToDTO;
    private final ModerationSettingsRepository moderationSettingsRepository;

    private static final String RETRIEVE_ALL_SETTINGS_REQUEST = "Received request for listing all moderation settings";
    private static final String RETRIEVE_ALL_SETTINGS_RESPONSE = "Returning response for listing all moderation settings request -> {}";
    private static final String RETRIEVE_SETTINGS_BY_ID_REQUEST = "Received request for retrieving moderation settings with id {}";
    private static final String RETRIEVE_SETTINGS_BY_ID_RESPONSE = "Returning response for listing moderation settings with id {} request -> {}";
    private static final String SAVE_SETTINGS_REQUEST = "Received request for saving moderation settings -> {}";
    private static final String SAVE_SETTINGS_RESPONSE = "Returning response for saving moderation settings request -> {}";
    private static final String UPDATE_SETTINGS_REQUEST = "Received request for updating moderation settings with ID {} -> {}";
    private static final String UPDATE_SETTINGS_RESPONSE = "Returning response for updating moderation settings with id {} request -> {}";
    private static final String DELETE_SETTINGS_REQUEST = "Received request for deleting moderation settings with ID {}";
    private static final String DELETE_SETTINGS_RESPONSE = "Returning response for deleting moderation settings with ID {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsController.class);

    @GetMapping
    public Mono<ResponseEntity<ApiResponse>> getAllSettings() {

        LOGGER.info(RETRIEVE_ALL_SETTINGS_REQUEST);
        return Mono.just(moderationSettingsRepository.findAll())
                .map(p -> p.stream()
                        .map(moderationSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .moderationSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_ALL_SETTINGS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @GetMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> getModerationSettingsById(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId) {

        LOGGER.info(RETRIEVE_SETTINGS_BY_ID_REQUEST, moderationSettingsId);
        return Mono.just(moderationSettingsRepository.findById(moderationSettingsId))
                .map(p -> p.map(moderationSettingsEntityToDTO)
                        .orElseThrow(() -> new RuntimeException()))
                .map(p -> Stream.of(p)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .moderationSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(RETRIEVE_SETTINGS_BY_ID_RESPONSE, moderationSettingsId, p);
                    return ResponseEntity.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(p);
                });
    }

    @PutMapping
    public Mono<ResponseEntity<ApiResponse>> saveModerationSettings(final ModerationSettings moderationSettings) {

        LOGGER.info(SAVE_SETTINGS_REQUEST, moderationSettings);
        return Mono.just(moderationSettingsDTOToEntity.apply(moderationSettings))
                .map(moderationSettingsRepository::save)
                .map(p -> Stream.of(p)
                        .map(moderationSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .moderationSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(SAVE_SETTINGS_RESPONSE, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @PatchMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<ApiResponse>> updateModerationSettingsById(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId,
            final ModerationSettings moderationSettings) {

        LOGGER.info(UPDATE_SETTINGS_REQUEST, moderationSettingsId, moderationSettings);
        return Mono.just(moderationSettingsDTOToEntity.apply(moderationSettings))
                .map(p -> {
                    p.setId(moderationSettingsId);
                    return moderationSettingsRepository.save(p);
                })
                .map(p -> Stream.of(p)
                        .map(moderationSettingsEntityToDTO)
                        .collect(Collectors.toList()))
                .map(p -> ApiResponse.builder()
                        .moderationSettings(p)
                        .build())
                .map(p -> {
                    LOGGER.info(UPDATE_SETTINGS_RESPONSE, moderationSettingsId, p);
                    return ResponseEntity.ok()
                            .body(p);
                });
    }

    @DeleteMapping("{moderation-settings-id}")
    public Mono<ResponseEntity<?>> deleteModerationSettingsById(
            @PathVariable(value = "moderation-settings-id") final String moderationSettingsId) {

        LOGGER.info(DELETE_SETTINGS_REQUEST, moderationSettingsId);
        return Mono.just(moderationSettingsId)
                .map(id -> {
                    moderationSettingsRepository.deleteById(id);
                    LOGGER.info(DELETE_SETTINGS_RESPONSE, moderationSettingsId);
                    return ResponseEntity.ok()
                            .build();
                });
    }
}
