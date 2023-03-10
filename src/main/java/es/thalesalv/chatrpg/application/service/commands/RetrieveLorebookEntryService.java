package es.thalesalv.chatrpg.application.service.commands;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.application.translator.LorebookEntryToDTOTranslator;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookDTO;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

@Service
@Transactional
@RequiredArgsConstructor
public class RetrieveLorebookEntryService implements CommandService {

    private final ObjectMapper objectMapper;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveLorebookEntryService.class);
    private static final String ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Entered command for retrieving lore entry");
            event.deferReply();
            final OptionMapping entryId = event.getOption("lorebook-entry-id");
            if (entryId != null) {
                final LorebookRegex entry = lorebookRegexRepository.findByLorebookEntry(LorebookEntry.builder()
                        .id(UUID.fromString(entryId.getAsString())).build())
                        .orElseThrow(LorebookEntryNotFoundException::new);

                final LorebookDTO dto = lorebookEntryToDTOTranslator.apply(entry);
                final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                        .writerWithDefaultPrettyPrinter().writeValueAsString(dto);

                event.reply(MessageFormat.format(ENTRY_RETRIEVED, dto.getName(), loreEntryJson))
                            .setEphemeral(true).complete();

                return;
            }

            final List<LorebookDTO> entries = lorebookRegexRepository.findAll()
                    .stream()
                    .map(entry -> lorebookEntryToDTOTranslator.apply(entry))
                    .collect(Collectors.toList());

            if (entries.isEmpty()) {
                event.reply("There are no lorebook entries saved")
                        .setEphemeral(true)
                        .complete();

                return;
            }

            final String entriesJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(entries);

            final File file = File.createTempFile("lore-entries-", ".json");
            Files.write(file.toPath(), entriesJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            final FileUpload fileUpload = FileUpload.fromData(file);

            event.replyFiles(fileUpload)
                    .setEphemeral(true)
                    .complete();

            fileUpload.close();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing entry data.", e);
            event.reply(ERROR_RETRIEVE).setEphemeral(true).complete();
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info("User tried to retrieve an entry that does not exist");
            event.reply("The entry queried does not exist.").setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("An error occurred while retrieving lorebook data", e);
            event.reply(ERROR_RETRIEVE).setEphemeral(true).complete();
        }
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        throw new UnsupportedOperationException("Unimplemented method 'handle'");
    }
}
