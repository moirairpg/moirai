package es.thalesalv.chatrpg.application.service;

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
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class ModelSettingsService {

    private final JDA jda;
    private final ModelSettingsDTOToEntity moderationSettingsDTOToEntity;
    private final ModelSettingsEntityToDTO moderationSettingsEntityToDTO;

    private final ModelSettingsRepository moderationSettingsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSettingsService.class);

    public List<ModelSettings> retrieveAllModelSettings() {

        LOGGER.debug("Retrieving moderation settings data from request");
        return moderationSettingsRepository.findAll()
                .stream()
                .map(moderationSettingsEntityToDTO)
                .toList();
    }

    public ModelSettings retrieveModelSettingsById(final String moderationSettingsId) {

        LOGGER.debug("Retrieving moderation settings by ID data from request");
        return moderationSettingsRepository.findById(moderationSettingsId)
                .map(moderationSettingsEntityToDTO)
                .orElseThrow(ModelSettingsNotFoundException::new);
    }

    public ModelSettings saveModelSettings(final ModelSettings moderationSettings) {

        LOGGER.debug("Saving moderation settings data from request");
        return Optional.of(moderationSettingsDTOToEntity.apply(moderationSettings))
                .map(c -> {
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    return c;
                })
                .map(moderationSettingsRepository::save)
                .map(moderationSettingsEntityToDTO)
                .orElseThrow();
    }

    public ModelSettings updateModelSettings(final String moderationSettingsId,
            final ModelSettings moderationSettings) {

        LOGGER.debug("Updating moderation settings data from request");
        return Optional.of(moderationSettingsDTOToEntity.apply(moderationSettings))
                .map(c -> {
                    c.setOwner(Optional.ofNullable(c.getOwner())
                            .orElse(jda.getSelfUser()
                                    .getId()));

                    c.setId(moderationSettingsId);
                    return moderationSettingsRepository.save(c);
                })
                .map(moderationSettingsEntityToDTO)
                .orElseThrow();
    }

    public void deleteModelSettings(final String moderationSettingsId) {

        LOGGER.debug("Deleting moderation settings data from request");
        moderationSettingsRepository.deleteById(moderationSettingsId);
    }
}
