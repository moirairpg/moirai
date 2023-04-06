package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Component
@RequiredArgsConstructor
public class ModerationSettingsDTOToEntity implements Function<ModerationSettings, ModerationSettingsEntity> {

    private final JDA jda;

    @Override
    public ModerationSettingsEntity apply(ModerationSettings moderationSettings) {

        return ModerationSettingsEntity.builder()
                .id(moderationSettings.getId())
                .owner(Optional.ofNullable(moderationSettings.getOwner())
                        .orElse(jda.getSelfUser()
                                .getId()))
                .isAbsolute(moderationSettings.isAbsolute())
                .thresholds(moderationSettings.getThresholds())
                .build();
    }
}
