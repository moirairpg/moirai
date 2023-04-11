package es.thalesalv.chatrpg.application.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ModerationSettingsNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class ModerationSettingsService {

    private final JDA jda;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityToDTO;
    private final ModerationSettingsRepository moderationSettingsRepository;

    private static final String DEFAULT_ID = "0";
    private static final String SETTING_ID_NOT_FOUND = "moderation setting with id SETTING_ID could not be found in database.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsService.class);

    public List<ModerationSettings> retrieveAllModerationSettings() {

        LOGGER.debug("Retrieving moderation settings data from request");
        return moderationSettingsRepository.findAll()
                .stream()
                .filter(l -> !l.getId()
                        .equals(DEFAULT_ID))
                .map(moderationSettingsEntityToDTO)
                .toList();
    }

    public ModerationSettings retrieveModerationSettingsById(final String moderationSettingsId) {

        LOGGER.debug("Retrieving moderation settings by ID data from request");
        return moderationSettingsRepository.findById(moderationSettingsId)
                .map(moderationSettingsEntityToDTO)
                .orElseThrow(() -> new ModerationSettingsNotFoundException("Error retrieving moderation setting by id: "
                        + SETTING_ID_NOT_FOUND.replace("SETTING_ID", moderationSettingsId)));
    }

    public ModerationSettings saveModerationSettings(final ModerationSettings moderationSettings) {

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
                .orElseThrow(() -> new RuntimeException("Error saving moderation setting"));
    }

    public ModerationSettings updateModerationSettings(final String moderationSettingsId,
            final ModerationSettings moderationSettings) {

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
                .orElseThrow(() -> new ModerationSettingsNotFoundException("Error updating moderation setting: "
                        + SETTING_ID_NOT_FOUND.replace("SETTING_ID", moderationSettingsId)));
    }

    public void deleteModerationSettings(final String moderationSettingsId) {

        LOGGER.debug("Deleting moderation settings data from request");
        moderationSettingsRepository.deleteById(moderationSettingsId);
    }
}
