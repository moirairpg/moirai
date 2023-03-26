package es.thalesalv.chatrpg.application.service;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
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
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelConfigToEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfigYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@DependsOn("nanoId")
@RequiredArgsConstructor
public class IngestDefaultConfigService {

    private final JDA jda;
    private final ChannelConfigToEntity channelConfigToEntity;
    private final ChannelConfigRepository channelConfigRepository;
    private final PersonaRepository personaRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultConfigService.class);

    @PostConstruct
    public void ingestDefaultChannelConfig() throws StreamReadException, DatabindException, IOException {

        try {
            final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                    .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

            final ChannelConfigYaml yaml = objectMapper.readValue(new ClassPathResource("channel-config.yaml")
                    .getInputStream(), ChannelConfigYaml.class);

            LOGGER.info("Found default configs. Ingesting them to database.");

            int i = 1;
            for (ChannelConfig config : yaml.getConfigs()) {
                final String id = String.valueOf(i);
                config.setId(id);
                config.getPersona().setId(id);
                config.getSettings().getModelSettings().setId(id);
                config.getSettings().getModerationSettings().setId(id);
                config.setOwner(jda.getSelfUser().getId());
                config.getPersona().setOwner((jda.getSelfUser().getId()));
                config.getSettings().getModelSettings().setOwner((jda.getSelfUser().getId()));
                config.getSettings().getModerationSettings().setOwner((jda.getSelfUser().getId()));

                final ChannelConfigEntity entity = channelConfigToEntity.apply(config);
                personaRepository.save(entity.getPersona());
                moderationSettingsRepository.save(entity.getModerationSettings());
                modelSettingsRepository.save(entity.getModelSettings());
                channelConfigRepository.save(entity);
                i++;
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Default configurations not found. Proceeding without them.");
        }
    }
}
