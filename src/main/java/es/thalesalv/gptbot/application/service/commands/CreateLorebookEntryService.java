package es.thalesalv.gptbot.application.service.commands;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.gptbot.application.translator.LorebookEntryToDTOTranslator;
import es.thalesalv.gptbot.domain.model.openai.dto.LorebookDTO;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@RequiredArgsConstructor
public class CreateLorebookEntryService implements CommandService {

    private final ObjectMapper objectMapper;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;

    private static final String ERROR_CREATE = "There was an error parsing your request. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLorebookEntryService.class);
    private static final String LORE_ENTRY_CREATED = "Lore entry with name **{0}** created. Don''t forget to save this ID!\n```json\n{1}\n```";

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Showing modal for character creation");
        final Modal modal = buildEntryCreationModal();
        event.replyModal(modal).queue();
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received data from character creation modal -> {}", event.getValues());
            event.deferReply();
            final User author = event.getMember().getUser();
            final String entryName = event.getValue("lorebook-entry-name").getAsString();
            final String entryRegex = event.getValue("lorebook-entry-regex").getAsString();
            final String entryDescription = event.getValue("lorebook-entry-desc").getAsString();
            final String entryPlayerCharacter = event.getValue("lorebook-entry-player").getAsString();
            final boolean isPlayerCharacter = entryPlayerCharacter.equals("y");
            final UUID lorebookEntryId = Generators.randomBasedGenerator().generate();
            final UUID lorebookRegexId = Generators.randomBasedGenerator().generate();
            final LorebookRegex insertedEntry = insertEntry(author, entryName, entryRegex,
                    entryDescription, lorebookEntryId, lorebookRegexId, isPlayerCharacter);

            final LorebookDTO loreItem = lorebookEntryToDTOTranslator.apply(insertedEntry);
            final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(loreItem);

            event.reply(MessageFormat.format(LORE_ENTRY_CREATED,
                            insertedEntry.getLorebookEntry().getName(), loreEntryJson))
                    .setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("An error occurred while creating lore entry", e);
            event.reply(ERROR_CREATE).setEphemeral(true).complete();
        }
    }

    private Modal buildEntryCreationModal() {

        LOGGER.debug("Building entry creation modal");
        final TextInput lorebookEntryName = TextInput
                .create("lorebook-entry-name", "Name", TextInputStyle.SHORT)
                .setPlaceholder("Forest of the Talking Trees")
                .setRequired(true)
                .build();

        final TextInput lorebookEntryRegex = TextInput
                .create("lorebook-entry-regex", "Regular Expression (optional)", TextInputStyle.SHORT)
                .setPlaceholder("/(Rain|)Forest of the (Talking|Speaking) Trees/gi")
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create("lorebook-entry-desc", "Lorebook Entry Name", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setMaxLength(150)
                .setRequired(true)
                .build();

        final TextInput lorebookEntryPlayer = TextInput
                .create("lorebook-entry-player", "Is this a player character?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create("create-lorebook-entry-data", "Lorebook Entry Creation")
                .addComponents(ActionRow.of(lorebookEntryName), ActionRow.of(lorebookEntryRegex),
                        ActionRow.of(lorebookEntryDescription), ActionRow.of(lorebookEntryPlayer))
                .build();
    }

    private LorebookRegex insertEntry(final User author, final String entryName, final String entryRegex,
            final String entryDescription, final UUID lorebookEntryId, final UUID lorebookRegexId, final boolean isPlayerCharacter) {

        final LorebookEntry insertedEntry = lorebookRepository.save(LorebookEntry.builder()
                .id(lorebookEntryId)
                .name(entryName)
                .description(entryDescription)
                .playerDiscordId(Optional.of(author.getId())
                        .filter(a -> isPlayerCharacter)
                        .orElse(null))
                .build());

        return lorebookRegexRepository.save(LorebookRegex.builder()
                .id(lorebookRegexId)
                .regex(Optional.ofNullable(entryRegex).orElse(entryName))
                .lorebookEntry(insertedEntry)
                .build());
    }
}
