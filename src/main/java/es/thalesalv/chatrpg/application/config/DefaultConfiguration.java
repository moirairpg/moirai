package es.thalesalv.chatrpg.application.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.repository.PersonaRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.application.service.ModerationSettingsService;
import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;

@Configuration
@DependsOn("nanoId")
@RequiredArgsConstructor
public class DefaultConfiguration {

    private final JDA jda;
    private final PersonaService personaService;
    private final LorebookService lorebookService;
    private final WorldService worldService;
    private final ModerationSettingsService moderationSettingsService;
    private final ChannelConfigService channelConfigService;

    private final ModerationSettingsRepository moderationSettingsRepository;
    private final PersonaRepository personaRepository;
    private final WorldRepository worldRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private final ObjectMapper yamlObjectMapper;

    private static final String CHCONF_YAML_FILE_PATH = "defaults/channel-configs.yaml";
    private static final String PERSONAS_YAML_FILE_PATH = "defaults/personas.yaml";
    private static final String WORLDS_FILE_PATH = "defaults/worlds.yaml";
    private static final String MODER_SETS_FILE_PATH = "defaults/moderation-settings.yaml";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfiguration.class);

    @PostConstruct
    public void init() throws StreamReadException, DatabindException, IOException {

        try {
            LOGGER.debug("Initiating default values ingestion process");
            final SelfUser bot = jda.getSelfUser();
            final InputStream configsYamlFile = new ClassPathResource(CHCONF_YAML_FILE_PATH).getInputStream();
            final List<ChannelConfig> defaultConfigs = yamlObjectMapper.readValue(configsYamlFile,
                    new TypeReference<List<ChannelConfig>>() {
                    });

            final InputStream personasYamlFile = new ClassPathResource(PERSONAS_YAML_FILE_PATH).getInputStream();
            final List<Persona> defaultPersonas = yamlObjectMapper.readValue(personasYamlFile,
                    new TypeReference<List<Persona>>() {
                    });

            final InputStream worldsYamlFile = new ClassPathResource(WORLDS_FILE_PATH).getInputStream();
            final List<World> defaultWorlds = yamlObjectMapper.readValue(worldsYamlFile,
                    new TypeReference<List<World>>() {
                    });

            final InputStream moderSetsYaml = new ClassPathResource(MODER_SETS_FILE_PATH).getInputStream();
            final List<ModerationSettings> defaultModSets = yamlObjectMapper.readValue(moderSetsYaml,
                    new TypeReference<List<ModerationSettings>>() {
                    });

            defaultPersonas.stream()
                    .filter(p -> !personaRepository.existsById(p.getId()))
                    .forEach(persona -> {
                        LOGGER.info("Default persona named {} not in DB. Ingesting it.", persona.getName());
                        persona.setOwner(bot.getId());
                        personaService.savePersona(persona);
                    });

            defaultWorlds.stream()
                    .filter(w -> !worldRepository.existsById(w.getId()))
                    .forEach(world -> {
                        LOGGER.info("Default world named {} not in DB. Ingesting it.", world.getName());
                        final Lorebook lorebook = world.getLorebook();
                        lorebook.setOwner(bot.getId());
                        lorebookService.saveLorebook(lorebook);

                        world.setOwner(bot.getId());
                        worldService.saveWorld(world);
                    });

            defaultModSets.stream()
                    .filter(m -> !moderationSettingsRepository.existsById(m.getId()))
                    .forEach(modsets -> {
                        LOGGER.info("Default moderation setting with ID {} not in DB. Ingesting it.", modsets.getId());
                        modsets.setOwner(bot.getId());
                        moderationSettingsService.saveModerationSettings(modsets);
                    });

            defaultConfigs.stream()
                    .filter(c -> !channelConfigRepository.existsById(c.getId()))
                    .forEach(config -> {
                        LOGGER.info("Default config named {} not in DB. Ingesting it.", config.getName());
                        config.setOwner(bot.getId());
                        channelConfigService.saveChannelConfig(config);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
