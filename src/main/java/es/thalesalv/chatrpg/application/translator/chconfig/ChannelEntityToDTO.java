package es.thalesalv.chatrpg.application.translator.chconfig;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
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

    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelEntityToDTO.class);

    @Override
    public Channel apply(ChannelEntity t) {

        try {
            final Persona persona = objectMapper
                    .readValue(objectMapper.writeValueAsString(t.getChannelConfig().getPersona()), Persona.class);

            final ModerationSettings moderationSettings = objectMapper.readValue(
                    objectMapper.writeValueAsString(t.getChannelConfig().getModerationSettings()),
                    ModerationSettings.class);

            final ModelSettings modelSettings = objectMapper.readValue(
                            objectMapper.writeValueAsString(t.getChannelConfig().getModelSettings()), ModelSettings.class);

            final World world = objectMapper.readValue(
                            objectMapper.writeValueAsString(t.getWorld()), World.class);

            final ChannelConfig config = ChannelConfig.builder()
                    .id(t.getChannelConfig().getId())
                    .owner(t.getChannelConfig().getOwner())
                    .editPermissions(t.getChannelConfig().getEditPermissions())
                    .persona(persona)
                    .settings(Settings.builder()
                            .modelSettings(modelSettings)
                            .moderationSettings(moderationSettings)
                            .build())
                    .build();

            return Channel.builder()
                    .channelId(t.getChannelId())
                    .id(t.getId())
                    .channelConfig(config)
                    .world(world)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error building channel config from entity");
            throw new RuntimeException("Error building channel config from entity", e);
        }
    }
}