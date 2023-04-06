package es.thalesalv.chatrpg.application.service.api;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModelSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ModelSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ModelSettingsService {

    private final ModelSettingsDTOToEntity moderationSettingsServiceDTOToEntity;
    private final ModelSettingsEntityToDTO moderationSettingsServiceEntityToDTO;

    private final ModelSettingsRepository moderationSettingsServiceRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSettingsService.class);

    public List<ModelSettings> retrieveAllModelSettings() {

        LOGGER.debug("Retrieving moderation settings data from request");
        return moderationSettingsServiceRepository.findAll()
                .stream()
                .map(moderationSettingsServiceEntityToDTO)
                .toList();
    }

    public ModelSettings retrieveModelSettingsById(final String moderationSettingsServiceId) {

        LOGGER.debug("Retrieving moderation settings by ID data from request");
        return moderationSettingsServiceRepository.findById(moderationSettingsServiceId)
                .map(moderationSettingsServiceEntityToDTO)
                .orElseThrow(ModelSettingsNotFoundException::new);
    }

    public ModelSettings saveModelSettings(final ModelSettings moderationSettingsService) {

        LOGGER.debug("Saving moderation settings data from request");
        return Optional.of(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(moderationSettingsServiceRepository::save)
                .map(moderationSettingsServiceEntityToDTO)
                .orElseThrow();
    }

    public ModelSettings updateModelSettings(final String moderationSettingsServiceId,
            final ModelSettings moderationSettingsService) {

        LOGGER.debug("Updating moderation settings data from request");
        return Optional.of(moderationSettingsServiceDTOToEntity.apply(moderationSettingsService))
                .map(c -> {
                    c.setId(moderationSettingsServiceId);
                    return moderationSettingsServiceRepository.save(c);
                })
                .map(moderationSettingsServiceEntityToDTO)
                .orElseThrow();
    }

    public void deleteModelSettings(final String moderationSettingsServiceId) {

        LOGGER.debug("Deleting moderation settings data from request");
        moderationSettingsServiceRepository.deleteById(moderationSettingsServiceId);
    }
}
