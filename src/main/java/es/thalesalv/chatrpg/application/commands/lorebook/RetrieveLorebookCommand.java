package es.thalesalv.chatrpg.application.commands.lorebook;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.application.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.translator.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.application.translator.LorebookEntryToDTOTranslator;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

@Component
@Transactional
@RequiredArgsConstructor
public class RetrieveLorebookCommand extends DiscordCommand {

    private final ObjectMapper objectMapper;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final ChannelRepository channelRepository;
    private final ChannelEntityListToDTOList channelEntityListToDTOList;
    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveLorebookCommand.class);
    private static final String ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                    .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                    .findFirst()
                    .ifPresent(channel -> {
                        try {
                            final OptionMapping entryId = event.getOption("lorebook-entry-id");
                            if (entryId != null) {
                                retrieveLoreEntryById(entryId.getAsString(), event);
                                return;
                            }

                            retrieveAllLoreEntries(event);
                        } catch (JsonProcessingException e) {
                            LOGGER.error("Error serializing entry data.", e);
                            event.reply(ERROR_RETRIEVE).setEphemeral(true).complete();
                        } catch (IOException e) {
                            LOGGER.error("Error handling lore entries file.", e);
                            event.reply(ERROR_RETRIEVE).setEphemeral(true).complete();
                        }
                    });
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info("User tried to retrieve an entry that does not exist");
            event.reply("The entry queried does not exist.").setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("An error occurred while retrieving lorebook data", e);
            event.reply(ERROR_RETRIEVE).setEphemeral(true).complete();
        }
    }

    private void retrieveAllLoreEntries(final SlashCommandInteractionEvent event) throws IOException {

        final List<LorebookEntry> entries = lorebookRegexRepository.findAll()
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

        event.replyFiles(fileUpload).setEphemeral(true).complete();
        fileUpload.close();
    }

    private void retrieveLoreEntryById(final String entryId, final SlashCommandInteractionEvent event)
            throws JsonProcessingException {

        final LorebookRegexEntity entry = lorebookRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder()
                .id(entryId).build()).orElseThrow(LorebookEntryNotFoundException::new);

        final LorebookEntry dto = lorebookEntryToDTOTranslator.apply(entry);
        final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                .writerWithDefaultPrettyPrinter().writeValueAsString(dto);

        event.reply(MessageFormat.format(ENTRY_RETRIEVED, dto.getName(), loreEntryJson))
                    .setEphemeral(true).complete();
    }
}
