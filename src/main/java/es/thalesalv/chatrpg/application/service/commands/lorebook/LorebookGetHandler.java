package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.utils.FileUpload;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookGetHandler {

    private final ObjectWriter prettyPrintObjectMapper;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ChannelRepository channelRepository;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String CHANNEL_NO_CONFIG_ATTACHED = "This channel does not have a configuration with a valid world/lorebook attached to it.";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "Channel does not have configuration attached";
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String ENTRY_RETRIEVED = "Retrieved lore entry with name **{0}**.\n```json\n{1}```";
    private static final String ERROR_HANDLING_ENTRY = "Error handling lore entries file.";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving lorebook data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String QUERIED_ENTRY_NOT_FOUND = "The entry queried does not exist.";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookGetHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findById(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {
                        try {
                            final OptionMapping entryId = event.getOption("id");
                            final World world = channel.getChannelConfig()
                                    .getWorld();

                            checkPermissions(world, event);
                            if (entryId != null) {
                                retrieveLoreEntryById(entryId.getAsString(), world, event);
                                return channel;
                            }

                            retrieveLorebook(world, event);
                            return channel;
                        } catch (JsonProcessingException e) {
                            LOGGER.error(ERROR_SERIALIZATION, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        } catch (IOException e) {
                            LOGGER.error(ERROR_HANDLING_ENTRY, e);
                            event.reply(ERROR_RETRIEVE)
                                    .setEphemeral(true)
                                    .queue(m -> m.deleteOriginal()
                                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        }
                        return channel;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info(QUERIED_ENTRY_NOT_FOUND);
            event.reply(QUERIED_ENTRY_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.info(CHANNEL_CONFIG_NOT_FOUND);
            event.reply(CHANNEL_NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVE, e);
            event.reply(USER_ERROR_RETRIEVE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private void retrieveLorebook(final World world, final SlashCommandInteractionEvent event)
            throws JsonProcessingException, IOException {

        world.setOwnerDiscordId(event.getJDA()
                .retrieveUserById(world.getOwnerDiscordId())
                .complete()
                .getName());

        final String lorebookJson = prettyPrintObjectMapper.writeValueAsString(world.getLorebook());
        final File file = File.createTempFile("lorebook-", ".json");
        Files.write(file.toPath(), lorebookJson.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        final FileUpload fileUpload = FileUpload.fromData(file);

        event.replyFiles(fileUpload)
                .setEphemeral(true)
                .complete();

        fileUpload.close();
    }

    private void retrieveLoreEntryById(final String entryId, final World world,
            final SlashCommandInteractionEvent event) throws JsonProcessingException {

        final LorebookEntry entry = world.getLorebook()
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

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = world.getVisibility()
                .equals("private");

        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canRead = world.getReadPermissions()
                .contains(userId)
                || world.getWritePermissions()
                        .contains(userId);

        final boolean isAllowed = isOwner || canRead;
        if (isPrivate && !isAllowed) {
            event.reply("You don't have permission from the owner of this private world to see it")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
