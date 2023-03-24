package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.worlds.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigToEntity implements Function<ChannelConfig, ChannelConfigEntity> {

    private final WorldDTOToEntity worldDTOToEntity;
    private final PersonaDTOToEntity personaDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigToEntity.class);

    @Override
    public ChannelConfigEntity apply(ChannelConfig t) {

        try {
            final WorldEntity world = worldDTOToEntity.apply(t.getWorld());
            final PersonaEntity persona = personaDTOToEntity.apply(t.getPersona());
            final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(t.getSettings().getModelSettings());
            final ModerationSettingsEntity moderationSettings = moderationSettingsDTOToEntity.apply(t.getSettings().getModerationSettings());

            return ChannelConfigEntity.builder()
                    .editPermissions(t.getEditPermissions())
                    .id(t.getId())
                    .owner(t.getOwner())
                    .modelSettings(modelSettings)
                    .moderationSettings(moderationSettings)
                    .persona(persona)
                    .world(world)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error building channel config entity from from yaml", e);
            throw new RuntimeException("Error building channel config entity from from yaml", e);
        }
    }
}
