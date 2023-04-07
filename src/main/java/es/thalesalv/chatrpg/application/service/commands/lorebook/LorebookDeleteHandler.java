package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookDeleteHandler {

    private final ContextDatastore contextDatastore;
    private final ChannelEntityToDTO channelEntityToDTO;
    private final LorebookEntryRepository lorebookEntryRepository;
    private final ChannelRepository channelRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;
    private static final String MODAL_ID = "lb-delete";
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String CHANNEL_CONFIG_NOT_FOUND = "The requested channel configuration could not be found";
    private static final String LORE_ENTRY_DELETED = "Lore entry deleted.";
    private static final String QUERIED_ENTRY_NOT_FOUND = "The entry queried does not exist.";
    private static final String USER_UPDATE_WITHOUT_ID = "User tried to use update command without ID";
    private static final String UNKNOWN_ERROR_CAUGHT = "Exception caught while deleting lorebook entry";
    private static final String DELETION_CANCELED = "Deletion action canceled. Entry has not been deleted.";
    private static final String ERROR_DELETE = "There was an error parsing your request. Please try again.";
    private static final String USER_DELETE_ENTRY_NOT_FOUND = "User tried to delete an entry that does not exist";
    private static final String MISSING_ID_MESSAGE = "The ID of the entry is required for a delete action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookDeleteHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry deletion");
            final String entryId = event.getOption("id")
                    .getAsString();

            channelRepository.findById(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .ifPresentOrElse(channel -> {
                        final World world = channel.getChannelConfig()
                                .getWorld();

                        checkPermissions(world, event);
                        lorebookEntryRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder()
                                .id(entryId)
                                .build())
                                .orElseThrow(LorebookEntryNotFoundException::new);

                        contextDatastore.setEventData(EventData.builder()
                                .lorebookEntryId(entryId)
                                .build());

                        final Modal modal = buildEntryDeletionModal();
                        event.replyModal(modal)
                                .queue();

                    }, () -> event.reply(CHANNEL_CONFIG_NOT_FOUND)
                            .setEphemeral(true)
                            .queue(reply -> reply.deleteOriginal()
                                    .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info(USER_DELETE_ENTRY_NOT_FOUND);
            event.reply(QUERIED_ENTRY_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.info(USER_UPDATE_WITHOUT_ID);
            event.reply(MISSING_ID_MESSAGE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR_CAUGHT, e);
            event.reply(ERROR_DELETE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    public void handleModal(final ModalInteractionEvent event) {

        LOGGER.debug("Received data from lore entry deletion modal");
        event.deferReply();
        final boolean isUserSure = Optional.ofNullable(event.getValue(MODAL_ID))
                .filter(a -> a.getAsString()
                        .equals("y"))
                .isPresent();

        if (isUserSure) {
            final String id = contextDatastore.getEventData()
                    .getLorebookEntryId();

            final LorebookEntryEntity lorebookEntry = LorebookEntryEntity.builder()
                    .id(id)
                    .build();

            lorebookEntryRegexRepository.deleteByLorebookEntry(lorebookEntry);
            lorebookEntryRepository.delete(lorebookEntry);
            event.reply(LORE_ENTRY_DELETED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
            return;
        }
        event.reply(DELETION_CANCELED)
                .setEphemeral(true)
                .queue(m -> m.deleteOriginal()
                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
    }

    private Modal buildEntryDeletionModal() {

        LOGGER.debug("Building entry deletion modal");
        final TextInput deleteLoreEntry = TextInput
                .create(MODAL_ID, "Are you sure you want to delete this entry?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create(MODAL_ID, "Delete lore entry")
                .addComponents(ActionRow.of(deleteLoreEntry))
                .build();
    }

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final Lorebook lorebook = world.getLorebook();
        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = lorebook.getVisibility()
                .equals("private");

        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canRead = lorebook.getReadPermissions()
                .contains(userId);

        final boolean isAllowed = isOwner || canRead;
        if (isPrivate && !isAllowed) {
            event.reply("You don't have permission from the owner of this private lorebook to see it")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
