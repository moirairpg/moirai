package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.openai.dto.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

@Service
@Transactional
@RequiredArgsConstructor
public class RetrieveLorebookCommandService implements DiscordCommand {

    private final ObjectMapper objectMapper;
    private final ChannelRepository channelRepository;
    private final ChannelEntityToDTO channelEntityMapper;

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final Logger LOGGER = LoggerFactory.getLogger(RetrieveLorebookCommandService.class);
    private static final String ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findByChannelId(event.getChannel().getId()).stream()
                    .findFirst()
                    .map(channelEntityMapper::apply)
                    .ifPresent(channel -> {
                        try {
                            final World world = channel.getChannelConfig().getWorld();
                            final OptionMapping entryId = event.getOption("lorebook-entry-id");
                            if (entryId != null) {
                                retrieveLoreEntryById(entryId.getAsString(), world, event);
                                return;
                            }

                            retrieveAllLoreEntries(world, event);
                        } catch (JsonProcessingException e) {
                            LOGGER.error("Error serializing entry data.", e);
                            event.reply(ERROR_RETRIEVE).setEphemeral(true).queue(reply -> {
                                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                            });
                        } catch (IOException e) {
                            LOGGER.error("Error handling lore entries file.", e);
                            event.reply(ERROR_RETRIEVE).setEphemeral(true).queue(reply -> {
                                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                            });
                        }
                    });
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info("User tried to retrieve an entry that does not exist or is not part of the current world");
            event.reply("The entry queried does not exist or is not part of this channel's world.")
                    .setEphemeral(true).queue(reply -> {
                        reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                    });
        } catch (Exception e) {
            LOGGER.error("An error occurred while retrieving lorebook data", e);
            event.reply(ERROR_RETRIEVE).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        }
    }

    private void retrieveAllLoreEntries(final World world, final SlashCommandInteractionEvent event) throws IOException {

        final Set<LorebookEntry> entries = world.getLorebook().getEntries();
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

    private void retrieveLoreEntryById(final String entryId, final World world, final SlashCommandInteractionEvent event)
            throws JsonProcessingException {

        final LorebookEntry entry = world.getLorebook().getEntries().stream()
                    .filter(e -> e.getId().equals(entryId)).findFirst()
                    .orElseThrow(() -> new LorebookEntryNotFoundException());

        final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                .writerWithDefaultPrettyPrinter().writeValueAsString(entry);

        event.reply(MessageFormat.format(ENTRY_RETRIEVED, entry.getName(), loreEntryJson))
                    .setEphemeral(true).complete();
    }
}
