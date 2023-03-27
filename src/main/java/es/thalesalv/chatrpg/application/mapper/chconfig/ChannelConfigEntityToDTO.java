package es.thalesalv.chatrpg.application.mapper.chconfig;

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

@Component
@RequiredArgsConstructor
public class ChannelConfigEntityToDTO implements Function<ChannelConfigEntity, ChannelConfig> {

    private final WorldEntityToDTO worldEntityToDTO;
    private final PersonaEntityToDTO personaEntityToDTO;
    private final ModelSettingsEntityToDTO modelSettingsEntityToDTO;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityToDTO;

    @Override
    public ChannelConfig apply(ChannelConfigEntity t) {

        final World world = worldEntityToDTO.apply(t.getWorld());
        final Persona persona = personaEntityToDTO.apply(t.getPersona());
        final ModelSettings modelSettings = modelSettingsEntityToDTO.apply(t.getModelSettings());
        final ModerationSettings moderationSettings = moderationSettingsEntityToDTO.apply(t.getModerationSettings());
        return ChannelConfig.builder()
                .editPermissions(t.getEditPermissions())
                .id(t.getId())
                .owner(t.getOwner())
                .persona(persona)
                .world(world)
                .settings(Settings.builder()
                        .modelSettings(modelSettings)
                        .moderationSettings(moderationSettings)
                        .build())
                .build();
    }
}
