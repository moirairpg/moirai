package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.text.MessageFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.ContextDatastore;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.translator.LorebookEntryToDTOTranslator;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.openai.dto.CommandEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateLorebookCommandService extends DiscordCommand {

    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;
    private final ModerationService moderationService;
    private final ContextDatastore contextDatastore;
    private final ObjectMapper objectMapper;
    private final LorebookRepository lorebookRepository;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final ChannelRepository channelRepository;
    private final ChannelEntityListToDTOList channelEntityListToDTOList;

    private static final String ERROR_UPDATE = "There was an error parsing your request. Please try again.";
    private static final String ENTRY_UPDATED = "Lore entry with name {0} was updated.\n```json\n{1}```";
    private static final String MISSING_ID_MESSAGE = "The UUID of the entry is required for an update action. Please try again with the entry id.";
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLorebookCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry update");
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                    .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                    .findFirst()
                    .ifPresent(channel -> {
                        final String entryId = event.getOption("lorebook-entry-id").getAsString();
                        contextDatastore.setCommandEventData(CommandEventData.builder()
                                .lorebookEntryId(entryId).channelConfig(channel.getChannelConfig()).build());

                        final var entry = lorebookRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder().id(entryId).build())
                                .orElseThrow(LorebookEntryNotFoundException::new);

                        final Modal modalEntry = buildEntryUpdateModal(entry);
                        event.replyModal(modalEntry).queue();
                        return;
                    });

            event.reply("This command cannot be issued from this channel.").setEphemeral(true).complete();
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
            LOGGER.debug("Received data from lore entry update modal");
            event.deferReply();
            final String entryId = contextDatastore.getCommandEventData().getLorebookEntryId();
            final String updatedEntryName = event.getValue("lorebook-entry-name").getAsString();
            final String updatedEntryRegex = event.getValue("lorebook-entry-regex").getAsString();
            final String updatedEntryDescription = event.getValue("lorebook-entry-desc").getAsString();
            final String playerId = retrieveDiscordPlayerId(event.getValue("lorebook-entry-player"),
                    event.getUser().getId());

            final LorebookRegexEntity updatedEntry = updateEntry(updatedEntryDescription, entryId,
                    updatedEntryName, playerId, updatedEntryRegex);

            final LorebookEntry entry = lorebookEntryToDTOTranslator.apply(updatedEntry);
            final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(entry);

            moderationService.moderate(loreEntryJson, contextDatastore.getCommandEventData(), event).subscribe(response -> {
                event.reply(MessageFormat.format(ENTRY_UPDATED,
                updatedEntry.getLorebookEntry().getName(), loreEntryJson))
                        .setEphemeral(true).complete();
            });
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing entry data into JSON", e);
            event.reply(ERROR_UPDATE).setEphemeral(true).complete();
        }
    }

    private LorebookRegexEntity updateEntry(final String description, final String entryId, final String name,
            final String playerId, final String entryRegex) {

        final LorebookEntryEntity lorebookEntry = LorebookEntryEntity.builder()
                .description(description)
                .id(entryId)
                .name(name)
                .playerDiscordId(playerId)
                .build();

        return lorebookRegexRepository.findByLorebookEntry(lorebookEntry)
                .map(re -> {
                    final LorebookRegexEntity lorebookRegex = LorebookRegexEntity.builder()
                            .id(re.getId())
                            .regex(Optional.ofNullable(entryRegex)
                                    .filter(StringUtils::isNotBlank)
                                    .orElse(name))
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

    private Modal buildEntryUpdateModal(final LorebookRegexEntity lorebookRegex) {

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
                .create("lorebook-entry-regex", "Regular expression (optional)", TextInputStyle.SHORT)
                .setValue(regex)
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create("lorebook-entry-desc", "Description", TextInputStyle.PARAGRAPH)
                .setValue(lorebookRegex.getLorebookEntry().getDescription())
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
