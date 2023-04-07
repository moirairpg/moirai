package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRegexRepository;
import es.thalesalv.chatrpg.adapters.data.repository.LorebookEntryRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookDTOToEntity;
import es.thalesalv.chatrpg.application.mapper.lorebook.LorebookEntryEntityToDTO;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookEditHandler {

    private final ChannelEntityToDTO channelEntityToDTO;
    private final LorebookDTOToEntity lorebookDTOToEntity;
    private final LorebookEntryEntityToDTO lorebookEntryEntityToDTO;
    private final ContextDatastore contextDatastore;
    private final ObjectWriter prettyPrintObjectMapper;
    private final ModerationService moderationService;
    private final ChannelRepository channelRepository;
    private final LorebookEntryRepository lorebookRepository;
    private final LorebookEntryRegexRepository lorebookEntryRegexRepository;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String ID_OPTION = "id";
    private static final String FIELD_NAME = "lb-entry-name";
    private static final String FIELD_REGEX = "lb-entry-regex";
    private static final String FIELD_DESCRIPTION = "lb-entry-desc";
    private static final String FIELD_PLAYER = "lb-entry-player";
    private static final String MODAL_ID = "lb-edit";
    private static final String ERROR_PARSING_JSON = "Error parsing entry data into JSON";
    private static final String USER_UPDATE_ENTRY_NOT_FOUND = "The entry queried does not exist.";
    private static final String UNKNOWN_ERROR_CAUGHT = "Exception caught while updating lorebook entry";
    private static final String ENTRY_UPDATED = "Lore entry with name {0} was updated.\n```json\n{1}```";
    private static final String COMMAND_WRONG_CHANNEL = "This command cannot be issued from this channel.";
    private static final String ERROR_UPDATE = "There was an error parsing your request. Please try again.";
    private static final String USER_UPDATE_COMMAND_WITHOUT_ID = "User tried to use update command without ID";
    private static final String MISSING_ID_MESSAGE = "The ID of the entry is required for an update action. Please try again with the entry ID.";

    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookEditHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry update");
            channelRepository.findById(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .ifPresentOrElse(channel -> {
                        final String entryId = event.getOption(ID_OPTION)
                                .getAsString();

                        final World world = channel.getChannelConfig()
                                .getWorld();

                        checkPermissions(world, event);
                        saveEventDataToContext(entryId, channel, event.getChannel());
                        final LorebookEntryRegexEntity entry = buildEntity(entryId);
                        final Modal modalEntry = buildEntryUpdateModal(entry);
                        event.replyModal(modalEntry)
                                .queue();
                    }, () -> event.reply(COMMAND_WRONG_CHANNEL)
                            .setEphemeral(true)
                            .queue(reply -> reply.deleteOriginal()
                                    .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
        } catch (MissingRequiredSlashCommandOptionException e) {
            LOGGER.info(USER_UPDATE_COMMAND_WITHOUT_ID);
            event.reply(MISSING_ID_MESSAGE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (LorebookEntryNotFoundException e) {
            LOGGER.info(USER_UPDATE_ENTRY_NOT_FOUND);
            event.reply(USER_UPDATE_ENTRY_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR_CAUGHT, e);
            event.reply(ERROR_UPDATE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    public void handleModal(final ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received data from lore entry update modal");
            event.deferReply();
            final EventData eventData = contextDatastore.getEventData();
            final World world = eventData.getChannelDefinitions()
                    .getChannelConfig()
                    .getWorld();

            final String entryId = contextDatastore.getEventData()
                    .getLorebookEntryId();

            final String updatedEntryName = event.getValue(FIELD_NAME)
                    .getAsString();

            final String updatedEntryRegex = event.getValue(FIELD_REGEX)
                    .getAsString();

            final String updatedEntryDescription = event.getValue(FIELD_DESCRIPTION)
                    .getAsString();

            final String playerId = retrieveDiscordPlayerId(event.getValue(FIELD_PLAYER), event.getUser()
                    .getId());

            final LorebookEntryRegexEntity updatedEntry = updateEntry(updatedEntryDescription, entryId,
                    updatedEntryName, playerId, updatedEntryRegex, world);

            final LorebookEntry entry = lorebookEntryEntityToDTO.apply(updatedEntry);
            final String loreEntryJson = prettyPrintObjectMapper.writeValueAsString(entry);

            moderationService.moderate(loreEntryJson, contextDatastore.getEventData(), event)
                    .subscribe(response -> event
                            .reply(MessageFormat.format(ENTRY_UPDATED, updatedEntry.getLorebookEntry()
                                    .getName(), loreEntryJson))
                            .setEphemeral(true)
                            .queue(m -> m.deleteOriginal()
                                    .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
        } catch (JsonProcessingException e) {
            LOGGER.error(ERROR_PARSING_JSON, e);
            event.reply(ERROR_UPDATE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private LorebookEntryRegexEntity updateEntry(final String description, final String entryId, final String name,
            final String playerId, final String entryRegex, final World world) {

        final LorebookEntryEntity lorebookEntry = LorebookEntryEntity.builder()
                .description(description)
                .id(entryId)
                .name(name)
                .playerDiscordId(playerId)
                .build();

        final LorebookEntity lorebook = lorebookDTOToEntity.apply(world.getLorebook());
        return lorebookEntryRegexRepository.findByLorebookEntry(lorebookEntry)
                .map(re -> {
                    final LorebookEntryRegexEntity lorebookRegex = LorebookEntryRegexEntity.builder()
                            .id(re.getId())
                            .regex(Optional.ofNullable(entryRegex)
                                    .filter(StringUtils::isNotBlank)
                                    .orElse(name))
                            .lorebookEntry(lorebookEntry)
                            .lorebook(lorebook)
                            .build();

                    lorebookRepository.save(lorebookEntry);
                    lorebookEntryRegexRepository.save(lorebookRegex);
                    return lorebookRegex;
                })
                .orElseThrow(LorebookEntryNotFoundException::new);
    }

    private String retrieveDiscordPlayerId(final ModalMapping modalMapping, final String id) {

        return Optional.of(modalMapping.getAsString())
                .filter(a -> a.equals("y"))
                .map(a -> id)
                .orElse(null);
    }

    private Modal buildEntryUpdateModal(final LorebookEntryRegexEntity lorebookRegex) {

        LOGGER.debug("Building entry update modal");
        final TextInput lorebookEntryName = TextInput.create(FIELD_NAME, "Name", TextInputStyle.SHORT)
                .setValue(lorebookRegex.getLorebookEntry()
                        .getName())
                .setRequired(true)
                .build();

        final String regex = Optional.ofNullable(lorebookRegex.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookRegex.getLorebookEntry()
                        .getName());

        final TextInput lorebookEntryRegex = TextInput
                .create(FIELD_REGEX, "Regular expression (optional)", TextInputStyle.SHORT)
                .setValue(regex)
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create(FIELD_DESCRIPTION, "Description", TextInputStyle.PARAGRAPH)
                .setValue(lorebookRegex.getLorebookEntry()
                        .getDescription())
                .setRequired(true)
                .build();

        String isPlayerCharacter = StringUtils.isBlank(lorebookRegex.getLorebookEntry()
                .getPlayerDiscordId()) ? "n" : "y";

        final TextInput lorebookEntryPlayer = TextInput
                .create(FIELD_PLAYER, "Is this a player character?", TextInputStyle.SHORT)
                .setValue(isPlayerCharacter)
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create(MODAL_ID, "Lorebook Entry Update")
                .addComponents(ActionRow.of(lorebookEntryName), ActionRow.of(lorebookEntryRegex),
                        ActionRow.of(lorebookEntryDescription), ActionRow.of(lorebookEntryPlayer))
                .build();
    }

    private void saveEventDataToContext(final String entryId, final Channel channelDefinitions,
            final MessageChannelUnion channel) {

        contextDatastore.setEventData(EventData.builder()
                .lorebookEntryId(entryId)
                .currentChannel(channel)
                .channelDefinitions(channelDefinitions)
                .build());
    }

    private LorebookEntryRegexEntity buildEntity(final String entryId) {

        return lorebookEntryRegexRepository.findByLorebookEntry(LorebookEntryEntity.builder()
                .id(entryId)
                .build())
                .orElseThrow(LorebookEntryNotFoundException::new);
    }

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final Lorebook lorebook = world.getLorebook();
        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = Optional.ofNullable(lorebook.getVisibility())
                .orElse(StringUtils.EMPTY)
                .equals("private");

        final boolean isOwner = Optional.ofNullable(lorebook.getOwner())
                .orElse(StringUtils.EMPTY)
                .equals(userId);

        final boolean canRead = Optional.ofNullable(lorebook.getReadPermissions())
                .orElse(StringUtils.EMPTY)
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
