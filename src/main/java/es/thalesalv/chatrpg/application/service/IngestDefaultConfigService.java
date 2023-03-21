package es.thalesalv.chatrpg.application.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ModelSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ModerationSettingsRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.PersonaRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.WorldRepository;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelConfigToEntity;
import es.thalesalv.chatrpg.application.translator.lorebook.LorebookDTOToEntityTranslator;
import es.thalesalv.chatrpg.application.translator.worlds.WorldDTOToEntityTranslator;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfigYaml;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import es.thalesalv.chatrpg.domain.model.openai.dto.WorldsYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Service
@RequiredArgsConstructor
public class IngestDefaultConfigService {

    private final JDA jda;
    private final WorldDTOToEntityTranslator worldDTOToEntityTranslator;
    private final LorebookDTOToEntityTranslator lorebookDTOToEntryTranslator;
    private final ChannelConfigToEntity channelConfigToEntity;
    private final ChannelConfigRepository channelConfigRepository;
    private final PersonaRepository personaRepository;
    private final ModerationSettingsRepository moderationSettingsRepository;
    private final ModelSettingsRepository modelSettingsRepository;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final WorldRepository worldRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultConfigService.class);

    @PostConstruct
    public void ingestDefaults() throws StreamReadException, DatabindException, IOException {
        ingestDefaultChannelConfigs();
        ingestDefaultWorlds();
    }

    public void ingestDefaultChannelConfigs() throws StreamReadException, DatabindException, IOException {

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

    public void ingestDefaultWorlds() throws StreamReadException, DatabindException, IOException {

        try {
            final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory())
                    .setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);

            final WorldsYaml yaml = objectMapper.readValue(new ClassPathResource("worlds.yaml")
                    .getInputStream(), WorldsYaml.class);

            LOGGER.info("Found default worlds. Ingesting them to database.");

            int i = 1;
            for (World world : yaml.getWorlds()) {
                int j = 1;
                List<LorebookRegexEntity> entryEntities = new ArrayList<>();
                world.setId(String.valueOf(i));
                final WorldEntity worldEntity = worldDTOToEntityTranslator.apply(world);
                worldEntity.setLorebook(null);
                worldRepository.save(worldEntity);
                for (LorebookEntry entry : world.getLorebook()) {
                    final String entryId = String.valueOf(j);
                    entry.setId(entryId);
                    entry.setRegexId(entryId);
                    final LorebookRegexEntity entryEntity = lorebookDTOToEntryTranslator.apply(entry);
                    entryEntity.setWorld(worldEntity);
                    lorebookRepository.save(entryEntity.getLorebookEntry());
                    lorebookRegexRepository.save(entryEntity);
                    entryEntities.add(entryEntity);
                    j++;
                }
                i++;
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Default worlds not found. Proceeding without them.");
        }
    }
}
