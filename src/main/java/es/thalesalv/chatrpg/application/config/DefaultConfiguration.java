package es.thalesalv.chatrpg.application.config;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;

import es.thalesalv.chatrpg.application.service.ChannelConfigService;
import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.application.service.ModelSettingsService;
import es.thalesalv.chatrpg.application.service.ModerationSettingsService;
import es.thalesalv.chatrpg.application.service.PersonaService;
import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.ModerationSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.Settings;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

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

    private static final String DEFAULT_ID = "0";
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfiguration.class);

    @PostConstruct
    public void init() throws StreamReadException, DatabindException, IOException {

        LOGGER.debug("Initiating default values ingestion process");
        final String botId = jda.getSelfUser()
                .getId();

        final Persona persona = Persona.defaultPersona();
        final World world = World.defaultWorld();
        final ModerationSettings moderationSettings = ModerationSettings.defaultModerationSettings();
        final ModelSettings modelSettings = ModelSettings.defaultModelSettings();
        final Settings settings = Settings.builder()
                .modelSettings(modelSettings)
                .moderationSettings(moderationSettings)
                .build();

        persona.setOwner(botId);
        world.setOwner(botId);
        world.getLorebook()
                .setOwner(botId);
        moderationSettings.setOwner(botId);
        modelSettings.setOwner(botId);

        final ChannelConfig channelConfig = ChannelConfig.builder()
                .id(DEFAULT_ID)
                .owner(botId)
                .persona(persona)
                .world(world)
                .settings(settings)
                .build();

        personaService.savePersona(persona);
        lorebookService.saveLorebook(world.getLorebook());
        worldService.saveWorld(world);
        moderationSettingsService.saveModerationSettings(moderationSettings);
        modelSettingsService.saveModelSettings(modelSettings);
        channelConfigService.saveChannelConfig(channelConfig);
    }
}
