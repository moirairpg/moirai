package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.bot.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.bot.ModelSettings;
import es.thalesalv.chatrpg.domain.model.bot.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.bot.Persona;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigEntityToDTO implements Function<ChannelConfigEntity, ChannelConfig> {

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
                .id(channelConfigEntity.getId())
                .name(channelConfigEntity.getName())
                .ownerDiscordId(channelConfigEntity.getOwnerDiscordId())
                .readPermissions(channelConfigEntity.getReadPermissions())
                .writePermissions(channelConfigEntity.getWritePermissions())
                .persona(persona)
                .world(world)
                .modelSettings(modelSettings)
                .moderationSettings(moderationSettings)
                .build();
    }
}
