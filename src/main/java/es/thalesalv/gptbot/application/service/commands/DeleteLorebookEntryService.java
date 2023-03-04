package es.thalesalv.gptbot.application.service.commands;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.gptbot.application.config.CommandEventData;
import es.thalesalv.gptbot.domain.exception.MissingRequiredSlashCommandOptionException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteLorebookEntryService implements CommandService {

    private final ContextDatastore contextDatastore;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final String ERROR_DELETE = "There was an error parsing your request. Please try again.";
    private static final String DELETE_ENTRY_MESSAGE = "Are you sure you want to delete this entry?";
    private static final String MISSING_ID_MESSAGE = "The UUID of the entry is required for a delete action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteLorebookEntryService.class);
    private static final String LORE_ENTRY_DELETED = "Lore entry deleted.";

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Showing modal for character deletion");
            final UUID entryId = retrieveEntryId(event.getOption("lorebook-entry-id"));
            contextDatastore.setCommandEventData(CommandEventData.builder()
                    .lorebookEntryId(entryId).build());

            final Modal modal = buildEntryDeletionModal();
            event.replyModal(modal).queue();
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.debug("User tried to use update command without ID");
            event.reply(MISSING_ID_MESSAGE).setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("An error occurred while deleting the lore entry", e);
            event.reply(ERROR_DELETE).setEphemeral(true).complete();
        }
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        LOGGER.debug("Received data from character deletion modal");
        event.deferReply();
        final UUID id = contextDatastore.getCommandEventData().getLorebookEntryId();
        final LorebookEntry lorebookEntry = LorebookEntry.builder().id(id).build();
        lorebookRegexRepository.deleteByLorebookEntry(lorebookEntry);
        lorebookRepository.delete(lorebookEntry);
        event.reply(LORE_ENTRY_DELETED).setEphemeral(true).complete();
    }

    private Modal buildEntryDeletionModal() {

        LOGGER.debug("Building entry deletion modal");
        return Modal.create("delete-lorebook-entry-data", DELETE_ENTRY_MESSAGE).build();
    }

    private UUID retrieveEntryId(final OptionMapping eventOption) {

        return Optional.ofNullable(eventOption)
                    .filter(a -> StringUtils.isNotBlank(a.getAsString()))
                    .map(a -> UUID.fromString(a.getAsString()))
                    .orElseThrow(MissingRequiredSlashCommandOptionException::new);
    }
}
