package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModerationSettings;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ModerationSettingsEntityToDTO implements Function<ModerationSettingsEntity, ModerationSettings> {

    @Override
    public ModerationSettings apply(ModerationSettingsEntity t) {

        return ModerationSettings.builder()
                .id(t.getId())
                .owner(t.getOwner())
                .thresholds(t.getThresholds())
                .build();
    }
}