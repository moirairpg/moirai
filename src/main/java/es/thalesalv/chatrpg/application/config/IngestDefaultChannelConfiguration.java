package es.thalesalv.chatrpg.application.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelConfigDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldEntityToDTO;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfigYaml;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Configuration
@RequiredArgsConstructor
@DependsOn("ingestDefaultWorldConfiguration")
public class IngestDefaultChannelConfiguration {

    private final JDA jda;
    private final ObjectMapper yamlObjectMapper;

    private final WorldEntityToDTO worldEntityToDTO;
    private final WorldDTOToEntity worldDTOToEntity;
    private final ChannelConfigDTOToEntity channelConfigToEntity;

    private final WorldRepository worldRepository;
    private final PersonaRepository personaRepository;
    private final LorebookRepository lorebookRepository;
    private final ChannelConfigRepository channelConfigRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;

    private static final String PRIVATE = "private";
    private static final String DEFAULT_WORLD = "Default world";
    private static final String YAML_FILE_PATH = "channel-config.yaml";
    private static final String INGESTING_WORLD = "Ingesting channel config -> {}";
    private static final String DEFAULT_CONFIG_FOUND = "Found default configs. Ingesting them to database.";
    private static final String DEFAULT_CONFIG_NOT_FOUND = "Default configurations not found. Proceeding without them.";

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultChannelConfiguration.class);

    @PostConstruct
    public void ingestDefaultChannelConfig() throws StreamReadException, DatabindException, IOException {

        LOGGER.debug("Initiating default channel configuration ingestion process");
        try {

            final InputStream yamlFile = new ClassPathResource(YAML_FILE_PATH).getInputStream();
            final ChannelConfigYaml yaml = yamlObjectMapper.readValue(yamlFile, ChannelConfigYaml.class);

            LOGGER.info(DEFAULT_CONFIG_FOUND);

            final AtomicInteger i = new AtomicInteger(1);
            for (ChannelConfig config : yaml.getConfigs()) {

                LOGGER.debug(INGESTING_WORLD, config);

                final String botId = jda.getSelfUser()
                        .getId();
                final String id = String.valueOf(i.get());
                config.setId(id);
                config.getPersona()
                        .setId(id);
                config.getSettings()
                        .getModelSettings()
                        .setId(id);
                config.getSettings()
                        .getModerationSettings()
                        .setId(id);
                config.setOwner(botId);
                config.getPersona()
                        .setOwner(botId);
                config.getSettings()
                        .getModelSettings()
                        .setOwner(botId);
                config.getSettings()
                        .getModerationSettings()
                        .setOwner(botId);
                config.setWorld(worldRepository.findById(id)
                        .map(worldEntityToDTO)
                        .orElseGet(() -> buildEmptyWorld(botId, id)));

                final ChannelConfigEntity entity = channelConfigToEntity.apply(config);
                personaRepository.save(entity.getPersona());
                moderationSettingsRepository.save(entity.getModerationSettings());
                modelSettingsRepository.save(entity.getModelSettings());
                channelConfigRepository.save(entity);
                i.incrementAndGet();
            }
        } catch (FileNotFoundException e) {

            LOGGER.warn(DEFAULT_CONFIG_NOT_FOUND);
        }
    }

    private World buildEmptyWorld(String botId, String id) {

        final World world = World.builder()
                .id(id)
                .name(DEFAULT_WORLD)
                .owner(botId)
                .visibility(PRIVATE)
                .lorebook(Lorebook.builder()
                        .id(id)
                        .owner(botId)
                        .visibility(PRIVATE)
                        .entries(new HashSet<>())
                        .build())
                .build();

        final WorldEntity worldEntity = worldDTOToEntity.apply(world);
        lorebookRepository.save(worldEntity.getLorebook());
        worldRepository.save(worldEntity);
        return world;
    }
}
