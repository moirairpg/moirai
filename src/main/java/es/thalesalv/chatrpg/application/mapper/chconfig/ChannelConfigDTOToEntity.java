package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigDTOToEntity implements Function<ChannelConfig, ChannelConfigEntity> {

    private final PersonaDTOToEntity personaDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;

    @Override
    public ChannelConfigEntity apply(ChannelConfig t) {

        final PersonaEntity persona = personaDTOToEntity.apply(t.getPersona());
        final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(t.getSettings().getModelSettings());
        final ModerationSettingsEntity moderationSettings = moderationSettingsDTOToEntity.apply(t.getSettings().getModerationSettings());
        return ChannelConfigEntity.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .owner(t.getOwner())
                .persona(persona)
                .modelSettings(modelSettings)
                .moderationSettings(moderationSettings)
                .build();
    }
}
