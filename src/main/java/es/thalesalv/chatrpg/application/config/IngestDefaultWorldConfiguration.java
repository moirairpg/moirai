package es.thalesalv.chatrpg.application.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.world.WorldDTOToEntity;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import es.thalesalv.chatrpg.domain.model.chconf.WorldsYaml;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;

@Configuration
@DependsOn("nanoId")
@RequiredArgsConstructor
public class IngestDefaultWorldConfiguration {

    private final JDA jda;
    private final ObjectMapper yamlObjectMapper;
    private final WorldDTOToEntity worldDTOToEntity;
    private final LorebookDTOToEntity lorebookDTOToEntity;
    private final LorebookEntryDTOToEntity lorebookDTOToEntry;

    private final WorldRepository worldRepository;
    private final LorebookRepository lorebookRepository;
    private final LorebookEntryRepository lorebookEntryRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;

    private static final String YAML_FILE_PATH = "worlds.yaml";
    private static final String PRIVATE = "private";
    private static final String DEFAULT_LOREBOOK = "Default lorebook";
    private static final String INGESTING_WORLD = "Ingesting world -> {}";
    private static final String INGESTING_ENTRY = "Ingesting lorebook entry -> {}";
    private static final String DEFAULT_WORLDS_FOUND = "Found default worlds. Ingesting them to database.";
    private static final String DEFAULT_WORLDS_NOT_FOUND = "Default worlds not found. Proceeding without them.";

    private static final Logger LOGGER = LoggerFactory.getLogger(IngestDefaultWorldConfiguration.class);

    @PostConstruct
    public void ingestDefaultWorlds() throws StreamReadException, DatabindException, IOException {

        LOGGER.debug("Initiating default world ingestion process");
        try {
            final InputStream yamlFile = new ClassPathResource(YAML_FILE_PATH).getInputStream();
            final WorldsYaml yaml = yamlObjectMapper.readValue(yamlFile, WorldsYaml.class);

            LOGGER.info(DEFAULT_WORLDS_FOUND);

            final AtomicInteger i = new AtomicInteger(1);
            for (World world : yaml.getWorlds()) {
                LOGGER.debug(INGESTING_WORLD, world);

                world.setId(String.valueOf(i.get()));
                world.setOwner(jda.getSelfUser().getId());
                world.setLorebook(Optional.ofNullable(world.getLorebook())
                        .map(lorebook -> setLorebook(lorebook, i.get()))
                        .orElse(buildEmptyLorebook()));

                final WorldEntity worldEntity = worldDTOToEntity.apply(world);
                final LorebookEntity lorebookEntity = lorebookDTOToEntity.apply(world.getLorebook());
                lorebookRepository.save(lorebookEntity);

                int j = 1;
                for (LorebookEntry entry : world.getLorebook().getEntries()) {
                    LOGGER.debug(INGESTING_ENTRY, entry);

                    final String entryId = String.valueOf(j);
                    entry.setId(entryId);
                    entry.setRegexId(entryId);

                    final LorebookEntryRegexEntity entryEntity = lorebookDTOToEntry.apply(entry);
                    entryEntity.setLorebook(lorebookEntity);
                    lorebookEntryRepository.save(entryEntity.getLorebookEntry());
                    lorebookEntryRegexRepository.save(entryEntity);
                    j++;
                }

                worldRepository.save(worldEntity);
                i.incrementAndGet();
            }
        } catch (FileNotFoundException e) {
            LOGGER.warn(DEFAULT_WORLDS_NOT_FOUND);
        }
    }

    private Lorebook setLorebook(Lorebook lorebook, int id) {

        lorebook.setId(String.valueOf(id));
        lorebook.setOwner(jda.getSelfUser().getId());
        return lorebook;
    }

    private Lorebook buildEmptyLorebook() {

        return Lorebook.builder()
                .id("0")
                .owner(jda.getSelfUser().getId())
                .name(DEFAULT_LOREBOOK)
                .visibility(PRIVATE)
                .entries(new HashSet<>())
                .build();
    }
}