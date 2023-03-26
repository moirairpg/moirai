package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;

@Component
public class ModerationSettingsDTOToEntity implements Function<ModerationSettings, ModerationSettingsEntity> {

    @Override
    public ModerationSettingsEntity apply(ModerationSettings t) {

        return ModerationSettingsEntity.builder()
                .id(t.getId())
                .owner(t.getOwner())
                .isAbsolute(t.isAbsolute())
                .thresholds(t.getThresholds())
                .build();
    }
}
