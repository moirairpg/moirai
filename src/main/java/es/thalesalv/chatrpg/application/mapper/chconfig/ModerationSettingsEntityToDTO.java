package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.bot.ModerationSettings;

@Component
public class ModerationSettingsEntityToDTO implements Function<ModerationSettingsEntity, ModerationSettings> {

    @Override
    public ModerationSettings apply(ModerationSettingsEntity moderationSettingsEntity) {

        return ModerationSettings.builder()
                .id(moderationSettingsEntity.getId())
                .owner(moderationSettingsEntity.getOwner())
                .absolute(moderationSettingsEntity.isAbsolute())
                .thresholds(moderationSettingsEntity.getThresholds())
                .build();
    }
}
