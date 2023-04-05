package es.thalesalv.chatrpg.application.service.api;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ModerationSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationSettingsService {

    private final ModerationSettingsDTOToEntity moderationSettingsServiceDTOToEntity;
    private final ModerationSettingsEntityToDTO moderationSettingsServiceEntityToDTO;

    private final ModerationSettingsRepository moderationSettingsServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsService.class);

    public List<ModerationSettings> retrieveAllModerationSettings() {

        LOGGER.debug("Retrieving moderation settings data from request");
        return moderationSettingsServiceRepository.findAll()
                .stream()
                .map(moderationSettingsServiceEntityToDTO)
                .toList();
    }

    public List<ModerationSettings> retrieveModerationSettingsById(final String moderationSettingsServiceId) {

        LOGGER.debug("Retrieving moderation settings by ID data from request");
        return moderationSettingsServiceRepository.findById(moderationSettingsServiceId)
                .map(moderationSettingsServiceEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow(ModerationSettingsNotFoundException::new);
    }

    public List<ModerationSettings> saveModerationSettings(final ModerationSettings moderationSettingsService) {

        LOGGER.debug("Saving moderation settings data from request");
        return Optional.of(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(moderationSettingsServiceRepository::save)
                .map(moderationSettingsServiceEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow();
    }

    public List<ModerationSettings> updateModerationSettings(final String moderationSettingsServiceId,
            final ModerationSettings moderationSettingsService) {

        LOGGER.debug("Updating moderation settings data from request");
        return Optional.of(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(c -> {
                    c.setId(moderationSettingsServiceId);
                    return moderationSettingsServiceRepository.save(c);
                })
                .map(moderationSettingsServiceEntityToDTO)
                .map(Arrays::asList)
                .orElseThrow();
    }

    public void deleteModerationSettings(final String moderationSettingsServiceId) {

        LOGGER.debug("Deleting moderation settings data from request");
        moderationSettingsServiceRepository.deleteById(moderationSettingsServiceId);
    }
}
