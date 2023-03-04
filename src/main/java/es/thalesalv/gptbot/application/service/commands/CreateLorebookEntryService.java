package es.thalesalv.gptbot.application.service.commands;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.uuid.Generators;

import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLorebookEntryService.class);
    private static final String LORE_ENTRY_CREATED = "Lore entry with name **{0}** created.\n```json\n{1}\n```";

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Showing modal for character creation");
        final Modal modal = buildEntryCreationModal();
        event.replyModal(modal).queue();
    }

    @Override
    public void handle(ModalInteractionEvent event) {

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
            final LorebookEntry insertedEntry = lorebookRepository.save(LorebookEntry.builder()
                    .id(lorebookEntryId)
                    .name(entryName)
                    .description(entryDescription)
                    .playerDiscordId(Optional.of(author.getId()).filter(a -> isPlayerCharacter).orElse(null))
                    .build());

            final UUID lorebookRegexId = Generators.randomBasedGenerator().generate();
            lorebookRegexRepository.save(LorebookRegex.builder()
                    .id(lorebookRegexId)
                    .regex(Optional.ofNullable(entryRegex).orElse(entryName))
                    .lorebookEntry(insertedEntry)
                    .build());

            objectMapper.setSerializationInclusion(Include.NON_NULL);
            final PrivateChannel privateChannel = author.openPrivateChannel().complete();
            final String loreEntryJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(insertedEntry);
            privateChannel.sendMessage(MessageFormat.format(LORE_ENTRY_CREATED, insertedEntry.getName(), loreEntryJson)).complete();
            event.reply(MessageFormat.format(LORE_ENTRY_CREATED, insertedEntry.getName(), loreEntryJson))
                        .setEphemeral(true).complete();
        } catch (JsonProcessingException e) {
            LOGGER.error("Error serializing lore entry object", e);
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
                .build();

        return Modal.create("create-lorebook-entry-data", "Lorebook Entry Creation")
                .addComponents(ActionRow.of(lorebookEntryName), ActionRow.of(lorebookEntryRegex),
                        ActionRow.of(lorebookEntryDescription), ActionRow.of(lorebookEntryPlayer))
                .build();
    }
}
