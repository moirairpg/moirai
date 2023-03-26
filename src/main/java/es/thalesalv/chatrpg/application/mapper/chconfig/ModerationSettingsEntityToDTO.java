package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;

@Component
public class ModerationSettingsEntityToDTO implements Function<ModerationSettingsEntity, ModerationSettings> {

    @Override
    public ModerationSettings apply(ModerationSettingsEntity t) {

        return ModerationSettings.builder()
                .id(t.getId())
                .owner(t.getOwner())
                .absolute(t.isAbsolute())
                .thresholds(t.getThresholds())
                .build();
    }
}
