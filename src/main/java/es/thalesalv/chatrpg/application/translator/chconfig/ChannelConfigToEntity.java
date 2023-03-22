package es.thalesalv.chatrpg.application.translator.chconfig;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.ModerationSettingsEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.PersonaEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChannelConfigToEntity implements Function<ChannelConfig, ChannelConfigEntity> {

    private final ObjectMapper objectMapper;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigToEntity.class);

    @Override
    public ChannelConfigEntity apply(ChannelConfig t) {

        try {
            final ModelSettingsEntity modelSettings = objectMapper.readValue(
                    objectMapper.writeValueAsString(t.getSettings().getModelSettings()), ModelSettingsEntity.class);

            final ModerationSettingsEntity moderationSettings = objectMapper.readValue(
                    objectMapper.writeValueAsString(t.getSettings().getModerationSettings()), ModerationSettingsEntity.class);

            final PersonaEntity persona = objectMapper.readValue(
                    objectMapper.writeValueAsString(t.getPersona()), PersonaEntity.class);

            return ChannelConfigEntity.builder()
                    .editPermissions(t.getEditPermissions())
                    .id(t.getId())
                    .owner(t.getOwner())
                    .modelSettings(modelSettings)
                    .moderationSettings(moderationSettings)
                    .persona(persona)
                    .build();
        } catch (Exception e) {
            LOGGER.error("Error building channel config entity from from yaml", e);
            throw new RuntimeException("Error building channel config entity from from yaml", e);
        }
    }
}
