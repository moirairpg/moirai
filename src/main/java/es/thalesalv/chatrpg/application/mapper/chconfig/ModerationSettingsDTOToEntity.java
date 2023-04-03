package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;

@Component
public class ModerationSettingsDTOToEntity implements Function<ModerationSettings, ModerationSettingsEntity> {

    @Override
    public ModerationSettingsEntity apply(ModerationSettings moderationSettings) {

        return ModerationSettingsEntity.builder()
                .id(moderationSettings.getId())
                .owner(moderationSettings.getOwner())
                .isAbsolute(moderationSettings.isAbsolute())
                .thresholds(moderationSettings.getThresholds())
                .build();
    }
}
