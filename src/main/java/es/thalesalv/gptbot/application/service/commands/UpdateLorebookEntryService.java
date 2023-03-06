package es.thalesalv.gptbot.application.service.commands;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.gptbot.adapters.data.db.entity.LorebookRegex;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.gptbot.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.config.CommandEventData;
import es.thalesalv.gptbot.application.service.ModerationService;
import es.thalesalv.gptbot.application.translator.LorebookEntryToDTOTranslator;
import es.thalesalv.gptbot.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.gptbot.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.gptbot.domain.model.openai.dto.LorebookDTO;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateLorebookEntryService implements CommandService {

    private final BotConfig botConfig;
    private final ModerationService moderationService;
    private final ContextDatastore contextDatastore;
    private final ObjectMapper objectMapper;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;

    private static final String ERROR_UPDATE = "There was an error parsing your request. Please try again.";
    private static final String ENTRY_UPDATED = "Lore entry with name {0} was updated.\n```json\n{1}```";
    private static final String MISSING_ID_MESSAGE = "The UUID of the entry is required for an update action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLorebookEntryService.class);
    
    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Showing modal for character update");
            botConfig.getPersonas().forEach(persona -> {
                final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> event.getChannel().getId().equals(id));
                if (isCurrentChannel) {
                    contextDatastore.setPersona(persona);
                    final UUID entryId = retrieveEntryId(event.getOption("lorebook-entry-id"));
                    contextDatastore.setCommandEventData(CommandEventData.builder()
                            .lorebookEntryId(entryId).build());

                    final var entry = lorebookRegexRepository.findByLorebookEntry(LorebookEntry.builder().id(entryId).build())
                            .orElseThrow(LorebookEntryNotFoundException::new);

                    final Modal modalEntry = buildEntryUpdateModal(entry);
                    event.replyModal(modalEntry).queue();
                    return;
                }

                event.reply("This command cannot be issued from this channel.").setEphemeral(true).complete();
            });
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.info("User tried to use update command without ID");
            event.reply(MISSING_ID_MESSAGE).setEphemeral(true).complete();
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info("User tried to update an entry that does not exist");
            event.reply("The entry queried does not exist.").setEphemeral(true).complete();
        } catch (Exception e) {
            LOGGER.error("Exception caught while updating lorebook entry", e);
            event.reply(ERROR_UPDATE).setEphemeral(true).complete();
        }
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received data from character update modal");
            event.deferReply();
            final UUID entryId = contextDatastore.getCommandEventData().getLorebookEntryId();
            final String updatedEntryName = event.getValue("lorebook-entry-name").getAsString();
            final String updatedEntryRegex = event.getValue("lorebook-entry-regex").getAsString();
            final String updatedEntryDescription = event.getValue("lorebook-entry-desc").getAsString();
            final String playerId = retrieveDiscordPlayerId(event.getValue("lorebook-entry-player"),
                    event.getUser().getId());
            
            final LorebookRegex updatedEntry = updateEntry(updatedEntryDescription, entryId,
                    updatedEntryName, playerId, updatedEntryRegex);

            final LorebookDTO entry = lorebookEntryToDTOTranslator.apply(updatedEntry);
            final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(entry);

            moderationService.moderate(loreEntryJson, event).subscribe(response -> {
                event.reply(MessageFormat.format(ENTRY_UPDATED,
                updatedEntry.getLorebookEntry().getName(), loreEntryJson))
                        .setEphemeral(true).complete();
            });
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing entry data into JSON", e);
            event.reply(ERROR_UPDATE).setEphemeral(true).complete();
        }
    }

    private LorebookRegex updateEntry(final String description, final UUID entryId, final String name,
            final String playerId, final String entryRegex) {

        final LorebookEntry lorebookEntry = LorebookEntry.builder()
                .description(description)
                .id(entryId)
                .name(name)
                .playerDiscordId(playerId)
                .build();

        return lorebookRegexRepository.findByLorebookEntry(lorebookEntry)
                .map(re -> {
                    final LorebookRegex lorebookRegex = LorebookRegex.builder()
                            .id(re.getId())
                            .regex(Optional.ofNullable(entryRegex).orElse(name))
                            .lorebookEntry(lorebookEntry)
                            .build();

                    lorebookRepository.save(lorebookEntry);
                    lorebookRegexRepository.save(lorebookRegex);
                    return lorebookRegex;
                }).get();
    }

    private String retrieveDiscordPlayerId(final ModalMapping modalMapping, final String id) {

        return Optional.of(modalMapping.getAsString())
                .filter(a -> a.equals("y"))
                .map(a -> id)
                .orElse(null);
    }

    private UUID retrieveEntryId(final OptionMapping eventOption) {

        return Optional.ofNullable(eventOption)
                    .filter(a -> StringUtils.isNotBlank(a.getAsString()))
                    .map(a -> UUID.fromString(a.getAsString()))
                    .orElseThrow(MissingRequiredSlashCommandOptionException::new);
    }

    private Modal buildEntryUpdateModal(final LorebookRegex lorebookRegex) {

        LOGGER.debug("Building entry update modal");
        final TextInput lorebookEntryName = TextInput
                .create("lorebook-entry-name", "Name", TextInputStyle.SHORT)
                .setValue(lorebookRegex.getLorebookEntry().getName())
                .setRequired(true)
                .build();

        final String regex = Optional.ofNullable(lorebookRegex.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookRegex.getLorebookEntry().getName());

        final TextInput lorebookEntryRegex = TextInput
                .create("lorebook-entry-regex", "Regular Expression (optional)", TextInputStyle.SHORT)
                .setValue(regex)
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create("lorebook-entry-desc", "Lorebook Entry Name", TextInputStyle.PARAGRAPH)
                .setValue(lorebookRegex.getLorebookEntry().getDescription())
                .setMaxLength(150)
                .setRequired(true)
                .build();

        String isPlayerCharacter = StringUtils.isBlank(lorebookRegex.getLorebookEntry()
                .getPlayerDiscordId()) ? "n" : "y";

        final TextInput lorebookEntryPlayer = TextInput
                .create("lorebook-entry-player", "Is this a player character?", TextInputStyle.SHORT)
                .setValue(isPlayerCharacter)
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create("update-lorebook-entry-data","Lorebook Entry Update")
                .addComponents(ActionRow.of(lorebookEntryName), ActionRow.of(lorebookEntryRegex),
                        ActionRow.of(lorebookEntryDescription), ActionRow.of(lorebookEntryPlayer)).build();
    }
}
