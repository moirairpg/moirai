package es.thalesalv.chatrpg.application.configuration;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.worlds.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.worlds.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.worlds.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import es.thalesalv.chatrpg.domain.model.openai.dto.WorldsYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Configuration
@RequiredArgsConstructor
public class IngestDefaultWorldConfiguration {

    private final JDA jda;
    private final ObjectMapper yamlObjectMapper;
    private final WorldDTOToEntity worldDTOToEntityMapper;
    private final LorebookDTOToEntity lorebookDTOToEntity;
    private final LorebookEntryDTOToEntity lorebookDTOToEntry;
    private final WorldRepository worldRepository;
    private final LorebookRepository lorebookRepository;
    private final LorebookEntryRepository lorebookEntryRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultWorldConfiguration.class);

    @PostConstruct
    public void ingestDefaultWorlds() throws StreamReadException, DatabindException, IOException {

        LOGGER.debug("Initiating default world ingestion process");
        try {
            final WorldsYaml yaml = yamlObjectMapper.readValue(new ClassPathResource("worlds.yaml")
                    .getInputStream(), WorldsYaml.class);

            LOGGER.info("Found default worlds. Ingesting them to database.");

            int i = 1;
            for (World world : yaml.getWorlds()) {
                LOGGER.debug("Ingesting world -> {}", world);

                int j = 1;
                world.setId(String.valueOf(i));
                world.setOwner(jda.getSelfUser().getId());
                world.getLorebook().setId(String.valueOf(i));
                world.getLorebook().setOwner(jda.getSelfUser().getId());
                final WorldEntity worldEntity = worldDTOToEntityMapper.apply(world);
                final LorebookEntity lorebookEntity = lorebookDTOToEntity.apply(world.getLorebook());
                lorebookRepository.save(lorebookEntity);
                for (LorebookEntry entry : world.getLorebook().getEntries()) {
                    LOGGER.debug("Ingesting lorebook entry -> {}", entry);

                    final String entryId = String.valueOf(j);
                    entry.setId(entryId);
                    entry.setRegexId(entryId);

                    final LorebookRegexEntity entryEntity = lorebookDTOToEntry.apply(entry);
                    entryEntity.setLorebook(lorebookEntity);
                    lorebookEntryRepository.save(entryEntity.getLorebookEntry());
                    lorebookRegexRepository.save(entryEntity);
                    j++;
                }

                worldRepository.save(worldEntity);
                i++;
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn("Default worlds not found. Proceeding without them.");
        }
    }
}
