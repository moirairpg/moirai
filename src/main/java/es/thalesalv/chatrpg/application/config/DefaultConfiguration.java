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
import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.application.service.ModelSettingsService;
import es.thalesalv.chatrpg.application.service.ModerationSettingsService;
import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
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
    private final ModelSettingsService modelSettingsService;
    private final ChannelConfigService channelConfigService;
    private final ChannelConfigRepository channelConfigRepository;

    private final ObjectMapper yamlObjectMapper;

    private static final String YAML_FILE_PATH = "defaults.yaml";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfiguration.class);

    @PostConstruct
    public void init() throws StreamReadException, DatabindException, IOException {

        try {
            LOGGER.debug("Initiating default values ingestion process");
            final SelfUser bot = jda.getSelfUser();
            final InputStream yamlFile = new ClassPathResource(YAML_FILE_PATH).getInputStream();
            final List<ChannelConfig> defaultConfigs = yamlObjectMapper.readValue(yamlFile, new TypeReference<List<ChannelConfig>>(){});

            defaultConfigs.stream()
                    .filter(c -> !channelConfigRepository.existsById(c.getId()))
                    .forEach(config -> {
                        final World world = config.getWorld();
                        final Lorebook lorebook = world.getLorebook();
                        final Persona persona = config.getPersona();
                        final ModerationSettings modsets = config.getModerationSettings();
                        final ModelSettings modelsets = config.getModelSettings();

                        config.setOwner(bot.getId());
                        world.setOwner(bot.getId());
                        lorebook.setOwner(bot.getId());
                        persona.setOwner(bot.getId());
                        persona.setName(bot.getName());
                        modsets.setOwner(bot.getId());
                        modelsets.setOwner(bot.getId());

                        modelSettingsService.saveModelSettings(modelsets);
                        moderationSettingsService.saveModerationSettings(modsets);
                        personaService.savePersona(persona);
                        lorebookService.saveLorebook(lorebook);
                        worldService.saveWorld(world);
                        channelConfigService.saveChannelConfig(config);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
