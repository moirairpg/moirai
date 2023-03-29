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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

@Service
@Transactional
@RequiredArgsConstructor
public class GetLorebookCommandService implements DiscordCommand {

    private final ObjectWriter prettyPrintObjectMapper;
    private final ChannelEntityToDTO channelEntityToDTO;

    private final ChannelRepository channelRepository;

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final String CHANNEL_NO_CONFIG_ATTACHED = "This channel does not have a configuration with a valid world/lorebook attached to it.";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "Channel does not have configuration attached";
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String NO_ENTRIES_FOUND = "There are no lorebook entries saved";
    private static final String ERROR_HANDLING_ENTRY = "Error handling lore entries file.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving lorebook data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String QUERIED_ENTRY_NOT_FOUND = "The entry queried does not exist.";

    private static final Logger LOGGER = LoggerFactory.getLogger(GetLorebookCommandService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        try {

            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {

                        try {

                            final World world = channel.getChannelConfig()
                                    .getWorld();
                            final OptionMapping entryId = event.getOption("id");
                            if (entryId != null) {

                                retrieveLoreEntryById(entryId.getAsString(), world, event);
                                return channel;
                            }

                            retrieveAllLoreEntries(world, event);
                        } catch (JsonProcessingException e) {

                            LOGGER.error(ERROR_SERIALIZATION, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                        } catch (IOException e) {

                            LOGGER.error(ERROR_HANDLING_ENTRY, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                        }

                        return channel;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (LorebookEntryNotFoundException e) {

            LOGGER.info(QUERIED_ENTRY_NOT_FOUND);
            event.reply(QUERIED_ENTRY_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        } catch (ChannelConfigNotFoundException e) {

            LOGGER.info(CHANNEL_CONFIG_NOT_FOUND);
            event.reply(CHANNEL_NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        } catch (Exception e) {

            LOGGER.error(ERROR_RETRIEVE, e);
            event.reply(USER_ERROR_RETRIEVE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        }
    }

    private void retrieveAllLoreEntries(final World world, final SlashCommandInteractionEvent event)
            throws IOException {

        final Set<LorebookEntry> entries = world.getLorebook()
                .getEntries();
        if (entries.isEmpty()) {

            event.reply(NO_ENTRIES_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));

            return;
        }

        final String entriesJson = prettyPrintObjectMapper.writeValueAsString(entries);
        final File file = File.createTempFile("lore-entries-", ".json");
        Files.write(file.toPath(), entriesJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        final FileUpload fileUpload = FileUpload.fromData(file);

        event.replyFiles(fileUpload)
                .setEphemeral(true)
                .complete();
        fileUpload.close();
    }

    private void retrieveLoreEntryById(final String entryId, final World world,
            final SlashCommandInteractionEvent event) throws JsonProcessingException {

        final LorebookEntry entry = world.getLorebook()
                .getEntries()
                .stream()
                .filter(e -> e.getId()
                        .equals(entryId))
                .findFirst()
                .orElseThrow(LorebookEntryNotFoundException::new);

        final String loreEntryJson = prettyPrintObjectMapper.writeValueAsString(entry);
        event.reply(MessageFormat.format(ENTRY_RETRIEVED, entry.getName(), loreEntryJson))
                .setEphemeral(true)
                .complete();
    }
}
