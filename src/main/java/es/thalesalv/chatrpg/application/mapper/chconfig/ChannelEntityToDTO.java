package es.thalesalv.chatrpg.application.mapper.chconfig;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
import es.thalesalv.chatrpg.application.mapper.worlds.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.openai.dto.Channel;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import es.thalesalv.chatrpg.domain.model.openai.dto.Settings;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelEntityToDTO implements Function<ChannelEntity, Channel> {

    private final WorldEntityToDTO worldMapper;
    private final PersonaEntityToDTO personaMapper;
    private final ModelSettingsEntityToDTO modelSettingsEntityMapper;
    private final ModerationSettingsEntityToDTO moderationSettingsEntityMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEntityToDTO.class);

    @Override
    public Channel apply(ChannelEntity t) {

        try {
            final Persona persona = personaMapper.apply(t.getChannelConfig().getPersona());
            final ModelSettings modelSettings = modelSettingsEntityMapper.apply(t.getChannelConfig().getModelSettings());
            final ModerationSettings moderationSettings = moderationSettingsEntityMapper.apply(t.getChannelConfig().getModerationSettings());
            final World world = worldMapper.apply(t.getChannelConfig().getWorld());

            return Channel.builder()
                    .channelId(t.getChannelId())
                    .id(t.getId())
                    .channelConfig(ChannelConfig.builder()
                            .id(t.getChannelConfig().getId())
                            .owner(t.getChannelConfig().getOwner())
                            .editPermissions(t.getChannelConfig().getEditPermissions())
                            .persona(persona)
                            .world(world)
                            .settings(Settings.builder()
                                    .modelSettings(modelSettings)
                                    .moderationSettings(moderationSettings)
                                    .build())
                            .build())
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error building channel config from entity");
            throw new RuntimeException("Error building channel config from entity", e);
        }
    }
}