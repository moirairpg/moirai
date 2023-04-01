package es.thalesalv.chatrpg.adapters.rest.controller;

import java.util.List;
import java.util.stream.Collectors;

import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
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
@RequestMapping("/settings")
public class SettingsController {

    private final ModelSettingsEntityToDTO modelSettingsEntityToDTO;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityToDTO;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsController.class);

    @GetMapping("model")
    public Mono<List<ModelSettings>> getAllModelSettings() {

        LOGGER.debug("Received request for listing all model settings");
        return Mono.just(modelSettingsRepository.findAll())
                .map(modelSettings -> modelSettings.stream()
                        .map(modelSettingsEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("moderation")
    public Mono<List<ModerationSettings>> getAllModerationSettings() {

        LOGGER.debug("Received request for listing all moderation settings");
        return Mono.just(moderationSettingsRepository.findAll())
                .map(moderationSettings -> moderationSettings.stream()
                        .map(moderationSettingsEntityToDTO)
                        .collect(Collectors.toList()));
    }

    @GetMapping("model/{id}")
    public Mono<ModelSettings> getModelSettingById(@PathVariable(value = "id") final String modelSettingId) {

        LOGGER.debug("Received request for retrieving model settings with id {}", modelSettingId);
        return Mono.just(modelSettingsRepository.findById(modelSettingId))
                .map(modelSetting -> modelSetting.map(modelSettingsEntityToDTO)
                        .orElseThrow(RuntimeException::new));
    }

    @GetMapping("moderation/{id}")
    public Mono<ModerationSettings> getModerationSettingById(@PathVariable(value = "id") final String moderationSettingId) {

        LOGGER.debug("Received request for retrieving moderation settings with id {}", moderationSettingId);
        return Mono.just(moderationSettingsRepository.findById(moderationSettingId))
                .map(moderationSetting -> moderationSetting.map(moderationSettingsEntityToDTO)
                        .orElseThrow(RuntimeException::new));
    }
}
