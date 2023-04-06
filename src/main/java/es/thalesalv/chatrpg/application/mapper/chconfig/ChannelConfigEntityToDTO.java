package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.Settings;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Component
@RequiredArgsConstructor
public class ChannelConfigEntityToDTO implements Function<ChannelConfigEntity, ChannelConfig> {

    private final JDA jda;
    private final WorldEntityToDTO worldEntityToDTO;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final ModelSettingsEntityToDTO modelSettingsEntityToDTO;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityToDTO;

    @Override
    public ChannelConfig apply(ChannelConfigEntity channelConfigEntity) {

        final World world = worldEntityToDTO.apply(channelConfigEntity.getWorld());
        final Persona persona = personaEntityToDTO.apply(channelConfigEntity.getPersona());
        final ModelSettings modelSettings = modelSettingsEntityToDTO.apply(channelConfigEntity.getModelSettings());
        final ModerationSettings moderationSettings = moderationSettingsEntityToDTO
                .apply(channelConfigEntity.getModerationSettings());

        return ChannelConfig.builder()
                .editPermissions(channelConfigEntity.getEditPermissions())
                .id(channelConfigEntity.getId())
                .owner(Optional.ofNullable(channelConfigEntity.getOwner())
                        .orElse(jda.getSelfUser()
                                .getId()))
                .persona(persona)
                .world(world)
                .settings(Settings.builder()
                        .modelSettings(modelSettings)
                        .moderationSettings(moderationSettings)
                        .build())
                .build();
    }
}
