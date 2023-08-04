package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigDTOToEntity implements Function<ChannelConfig, ChannelConfigEntity> {

    private final WorldDTOToEntity worldDTOToEntity;
    private final PersonaDTOToEntity personaDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;

    @Override
    public ChannelConfigEntity apply(ChannelConfig channelConfig) {

        final WorldEntity world = worldDTOToEntity.apply(channelConfig.getWorld());
        final PersonaEntity persona = personaDTOToEntity.apply(channelConfig.getPersona());
        final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(channelConfig.getModelSettings());

        final ModerationSettingsEntity moderationSettings = moderationSettingsDTOToEntity
                .apply(channelConfig.getModerationSettings());

        return ChannelConfigEntity.builder()
                .id(channelConfig.getId())
                .name(channelConfig.getName())
                .owner(channelConfig.getOwner())
                .readPermissions(channelConfig.getReadPermissions())
                .writePermissions(channelConfig.getWritePermissions())
                .persona(persona)
                .modelSettings(modelSettings)
                .moderationSettings(moderationSettings)
                .world(world)
                .build();
    }
}
