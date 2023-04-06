package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Component
@RequiredArgsConstructor
public class ChannelConfigDTOToEntity implements Function<ChannelConfig, ChannelConfigEntity> {

    private final JDA jda;
    private final WorldDTOToEntity worldDTOToEntity;
    private final PersonaDTOToEntity personaDTOToEntity;
    private final ModelSettingsDTOToEntity modelSettingsDTOToEntity;
    private final ModerationSettingsDTOToEntity moderationSettingsDTOToEntity;

    @Override
    public ChannelConfigEntity apply(ChannelConfig channelConfig) {

        final WorldEntity world = worldDTOToEntity.apply(channelConfig.getWorld());
        final PersonaEntity persona = personaDTOToEntity.apply(channelConfig.getPersona());
        final ModelSettingsEntity modelSettings = modelSettingsDTOToEntity.apply(channelConfig.getSettings()
                .getModelSettings());

        final ModerationSettingsEntity moderationSettings = moderationSettingsDTOToEntity
                .apply(channelConfig.getSettings()
                        .getModerationSettings());

        return ChannelConfigEntity.builder()
                .editPermissions(channelConfig.getEditPermissions())
                .id(channelConfig.getId())
                .owner(Optional.ofNullable(channelConfig.getOwner())
                        .orElse(jda.getSelfUser()
                                .getId()))
                .persona(persona)
                .modelSettings(modelSettings)
                .moderationSettings(moderationSettings)
                .world(world)
                .build();
    }
}
