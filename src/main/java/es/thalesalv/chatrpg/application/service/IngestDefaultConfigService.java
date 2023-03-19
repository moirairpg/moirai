package es.thalesalv.chatrpg.application.service;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.translator.ChannelConfigToEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfigYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class IngestDefaultConfigService {

    private final JDA jda;
    private final ChannelConfigToEntity channelConfigToEntity;
    private final ChannelConfigRepository channelConfigRepository;
    private final PersonaRepository personaRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;

    @PostConstruct
    public void ingestDefaultChannelConfig() throws StreamReadException, DatabindException, IOException {

        final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

        final ChannelConfigYaml yaml = objectMapper.readValue(new ClassPathResource("channel-config.yaml")
                .getInputStream(), ChannelConfigYaml.class);

        int i = 1;
        for (ChannelConfig config : yaml.getConfigs()) {
            final String id = String.valueOf(i);
            config.setId(id);
            config.getPersona().setId(id);
            config.getSettings().getModelSettings().setId(id);
            config.getSettings().getModerationSettings().setId(id);
            config.setOwner(jda.getSelfUser().getId());

            final ChannelConfigEntity entity = channelConfigToEntity.apply(config);
            personaRepository.save(entity.getPersona());
            moderationSettingsRepository.save(entity.getModerationSettings());
            modelSettingsRepository.save(entity.getModelSettings());
            channelConfigRepository.save(entity);
            i++;
        }
    }
}
