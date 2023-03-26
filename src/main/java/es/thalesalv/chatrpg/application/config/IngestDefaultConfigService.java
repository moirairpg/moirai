package es.thalesalv.chatrpg.application.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelConfigDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfigYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Configuration
@DependsOn("nanoId")
@RequiredArgsConstructor
public class IngestDefaultConfigService {

    private final JDA jda;
    private final ObjectMapper yamlObjectMapper;

    private final PersonaRepository personaRepository;
    private final ChannelConfigDTOToEntity channelConfigToEntity;
    private final ChannelConfigRepository channelConfigRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;

    private static final String YAML_FILE_PATH = "channel-config.yaml";
    private static final String DEFAULT_CONFIG_FOUND = "Found default configs. Ingesting them to database.";
    private static final String DEFAULT_CONFIG_NOT_FOUND = "Default configurations not found. Proceeding without them.";

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultConfigService.class);

    @PostConstruct
    public void ingestDefaultChannelConfig() throws StreamReadException, DatabindException, IOException {

        try {
            final InputStream yamlFile = new ClassPathResource(YAML_FILE_PATH).getInputStream();
            final ChannelConfigYaml yaml = yamlObjectMapper.readValue(yamlFile, ChannelConfigYaml.class);

            LOGGER.info(DEFAULT_CONFIG_FOUND);

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
            LOGGER.warn(DEFAULT_CONFIG_NOT_FOUND);
        }
    }
}
