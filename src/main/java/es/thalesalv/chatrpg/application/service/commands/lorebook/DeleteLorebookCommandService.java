package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.ContextDatastore;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteLorebookCommandService implements DiscordCommand {

    private final ChannelEntityToDTO channelEntityMapper;
    private final ContextDatastore contextDatastore;
    private final LorebookRepository lorebookRepository;
    private final ChannelRepository channelRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final String DELETION_CANCELED = "Deletion action canceled. Entry has not been deleted.";
    private static final String ERROR_DELETE = "There was an error parsing your request. Please try again.";
    private static final String MISSING_ID_MESSAGE = "The UUID of the entry is required for a delete action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteLorebookCommandService.class);
    private static final String LORE_ENTRY_DELETED = "Lore entry deleted.";

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry deletion");
            final String entryId = event.getOption("lorebook-entry-id").getAsString();
            channelRepository.findByChannelId(event.getChannel().getId()).stream()
                    .findFirst()
                    .map(channelEntityMapper::apply)
                    .ifPresent(channel -> {
                        lorebookRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder().id(entryId).build())
                                .orElseThrow(LorebookEntryNotFoundException::new);
                        contextDatastore.setEventData(EventData.builder()
                                .lorebookEntryId(entryId).build());

                        final Modal modal = buildEntryDeletionModal();
                        event.replyModal(modal).queue();
                    });
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info("User tried to delete an entry that does not exist");
            event.reply("The entry queried does not exist.").setEphemeral(true).complete();
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.info("User tried to use update command without ID");
            event.reply(MISSING_ID_MESSAGE).setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("Exception caught while deleting lorebook entry", e);
            event.reply(ERROR_DELETE).setEphemeral(true).complete();
        }
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        LOGGER.debug("Received data from lore entry deletion modal");
        event.deferReply();
        final boolean isUserSure = Optional.ofNullable(event.getValue("lorebook-entry-delete"))
                .filter(a -> a.getAsString().equals("y"))
                .map(a -> true)
                .orElse(false);

        if (isUserSure) {
            final String id = contextDatastore.getEventData().getLorebookEntryId();
            final LorebookEntryEntity lorebookEntry = LorebookEntryEntity.builder().id(id).build();
            lorebookRegexRepository.deleteByLorebookEntry(lorebookEntry);
            lorebookRepository.delete(lorebookEntry);
            event.reply(LORE_ENTRY_DELETED).setEphemeral(true).complete();
            return;
        }

        event.reply(DELETION_CANCELED).setEphemeral(true).complete();
    }

    private Modal buildEntryDeletionModal() {

        LOGGER.debug("Building entry deletion modal");
        final TextInput deleteLoreEntry = TextInput
                .create("lorebook-entry-delete", "Are you sure you want to delete this entry?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create("delete-lorebook-entry-data", "Delete lore entry")
                .addComponents(ActionRow.of(deleteLoreEntry)).build();
    }
}
