package es.thalesalv.gptbot.application.service.commands;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
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
public class DeleteLorebookEntryService implements CommandService {

    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteLorebookEntryService.class);
    private static final String LORE_ENTRY_DELETED = "Lore entry deleted.";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Showing modal for character deletion");
        final Modal modal = buildEntryDeletionModal();
        event.replyModal(modal).queue();
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        LOGGER.debug("Received data from character deletion modal");
        event.deferReply();

        final UUID id = UUID.fromString(event.getValue("lorebook-entry-id").getAsString());
        final LorebookEntry lorebookEntry = LorebookEntry.builder().id(id).build();
        lorebookRegexRepository.deleteByLorebookEntry(lorebookEntry);
        lorebookRepository.delete(lorebookEntry);
        event.reply(LORE_ENTRY_DELETED).setEphemeral(true).complete();
    }

    private Modal buildEntryDeletionModal() {

        LOGGER.debug("Building entry deletion modal");
        final TextInput lorebookEntryId = TextInput
                .create("lorebook-entry-id", "Entry ID", TextInputStyle.SHORT)
                .setPlaceholder("d6ed274a-a2a7-418c-b336-486c0506b7cf")
                .setRequired(true)
                .setRequiredRange(36, 36)
                .build();

        return Modal.create("delete-lorebook-entry-data", "Delete lorebook entry")
                .addComponents(ActionRow.of(lorebookEntryId)).build();
    }
}
