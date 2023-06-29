package es.thalesalv.chatrpg.application.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.chconfig.ModerationSettingsEntityToDTO;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ModerationSettingsService.class);

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
}
