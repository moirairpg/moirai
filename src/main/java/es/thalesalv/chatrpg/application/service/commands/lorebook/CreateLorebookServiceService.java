package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.text.MessageFormat;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRegexRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.LorebookRepository;
import es.thalesalv.chatrpg.application.ContextDatastore;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.translator.lorebook.LorebookEntryToDTOTranslator;
import es.thalesalv.chatrpg.application.util.NanoId;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.LorebookEntry;
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
public class CreateLorebookServiceService implements DiscordCommand {

    private final ChannelEntityToDTO channelEntityMapper;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final ObjectMapper objectMapper;
    private final LorebookRepository lorebookRepository;
    private final ChannelRepository channelRepository;
    private final LorebookRegexRepository lorebookRegexRepository;
    private final LorebookEntryToDTOTranslator lorebookEntryToDTOTranslator;

    private static final String ERROR_CREATE = "There was an error parsing your request. Please try again.";
    private static final String LORE_ENTRY_CREATED = "Lore entry with name **{0}** created. Don''t forget to save this ID!\n```json\n{1}\n```";
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateLorebookServiceService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for lore entry creation");
        channelRepository.findByChannelId(event.getChannel().getId()).stream()
                .findFirst()
                .map(channelEntityMapper::apply)
                .ifPresent(channel -> {
                    contextDatastore.setEventData(EventData.builder()
                            .channelConfig(channel.getChannelConfig()).build());

                    final Modal modal = buildEntryCreationModal();
                    event.replyModal(modal).queue();
                    return;
                });

        event.reply("This command cannot be issued from this channel.").setEphemeral(true).complete();
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received data from lore entry creation modal -> {}", event.getValues());
            event.deferReply();
            final User author = event.getMember().getUser();
            final String entryName = event.getValue("lorebook-entry-name").getAsString();
            final String entryRegex = event.getValue("lorebook-entry-regex").getAsString();
            final String entryDescription = event.getValue("lorebook-entry-desc").getAsString();
            final String entryPlayerCharacter = event.getValue("lorebook-entry-player").getAsString();
            final boolean isPlayerCharacter = entryPlayerCharacter.equals("y");
            final String lorebookEntryId = NanoId.randomNanoId();
            final String lorebookRegexId = NanoId.randomNanoId();
            final LorebookRegexEntity insertedEntry = insertEntry(author, entryName, entryRegex,
                    entryDescription, lorebookEntryId, lorebookRegexId, isPlayerCharacter);

            final LorebookEntry loreItem = lorebookEntryToDTOTranslator.apply(insertedEntry);
            final String loreEntryJson = objectMapper.setSerializationInclusion(Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter().writeValueAsString(loreItem);

            moderationService.moderate(loreEntryJson, contextDatastore.getEventData(), event).subscribe(response -> {
                event.reply(MessageFormat.format(LORE_ENTRY_CREATED,
                                insertedEntry.getLorebookEntry().getName(), loreEntryJson))
                        .setEphemeral(true).complete();
            });
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
                .create("lorebook-entry-regex", "Regular expression (optional)", TextInputStyle.SHORT)
                .setPlaceholder("/(Rain|)Forest of the (Talking|Speaking) Trees/gi")
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create("lorebook-entry-desc", "Description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
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

    private LorebookRegexEntity insertEntry(final User author, final String entryName, final String entryRegex,
            final String entryDescription, final String lorebookEntryId, final String lorebookRegexId, final boolean isPlayerCharacter) {

        final LorebookEntryEntity insertedEntry = lorebookRepository.save(LorebookEntryEntity.builder()
                .id(lorebookEntryId)
                .name(entryName)
                .description(entryDescription)
                .playerDiscordId(Optional.of(author.getId())
                        .filter(a -> isPlayerCharacter)
                        .orElse(null))
                .build());

        return lorebookRegexRepository.save(LorebookRegexEntity.builder()
                .id(lorebookRegexId)
                .regex(Optional.ofNullable(entryRegex)
                        .filter(StringUtils::isNotBlank)
                        .orElse(entryName))
                .lorebookEntry(insertedEntry)
                .build());
    }
}
