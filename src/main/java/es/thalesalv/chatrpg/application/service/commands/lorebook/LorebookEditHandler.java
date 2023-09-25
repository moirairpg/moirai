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

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.WorldService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.exception.LorebookEntryNotFoundException;
import es.thalesalv.chatrpg.domain.exception.MissingRequiredSlashCommandOptionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.bot.Channel;
import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
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
    private final ContextDatastore contextDatastore;
    private final ObjectWriter prettyPrintObjectMapper;
    private final ModerationService moderationService;
    private final ChannelRepository channelRepository;
    private final WorldService worldService;

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

                        final String eventAuthorId = event.getUser()
                                .getId();

                        final World world = channel.getChannelConfig()
                                .getWorld();

                        checkPermissions(world, event);
                        final LorebookEntry entry = worldService.retrieveLorebookEntryById(entryId, eventAuthorId);

                        saveEventDataToContext(entry, channel, event.getChannel());
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

            final LorebookEntry updatedEntry = updateEntry(world, eventData, event);
            final String loreEntryJson = prettyPrintObjectMapper.writeValueAsString(updatedEntry);
            moderationService.moderateInteraction(loreEntryJson, contextDatastore.getEventData(), event)
                    .subscribe(response -> event
                            .reply(MessageFormat.format(ENTRY_UPDATED, updatedEntry.getName(), loreEntryJson))
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

    private String retrieveDiscordPlayerId(final ModalMapping modalMapping, final String id) {

        return Optional.of(modalMapping.getAsString())
                .filter(a -> a.equals("y"))
                .map(a -> id)
                .orElse(null);
    }

    private Modal buildEntryUpdateModal(final LorebookEntry lorebookEntry) {

        LOGGER.debug("Building entry update modal");
        final TextInput lorebookEntryName = TextInput.create(FIELD_NAME, "Name", TextInputStyle.SHORT)
                .setValue(lorebookEntry.getName())
                .setRequired(true)
                .build();

        final String regex = Optional.ofNullable(lorebookEntry.getRegex())
                .filter(StringUtils::isNotBlank)
                .orElse(lorebookEntry.getName());

        final TextInput lorebookEntryRegex = TextInput
                .create(FIELD_REGEX, "Regular expression (optional)", TextInputStyle.SHORT)
                .setValue(regex)
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create(FIELD_DESCRIPTION, "Description", TextInputStyle.PARAGRAPH)
                .setValue(lorebookEntry.getDescription())
                .setRequired(true)
                .build();

        String isPlayerCharacter = StringUtils.isBlank(lorebookEntry.getPlayerDiscordId()) ? "n" : "y";

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

    private void saveEventDataToContext(final LorebookEntry entry, final Channel channelDefinitions,
            final MessageChannelUnion channel) {

        contextDatastore.setEventData(EventData.builder()
                .lorebookEntry(entry)
                .currentChannel(channel)
                .channelDefinitions(channelDefinitions)
                .build());
    }

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = world.getVisibility()
                .equals("private");

        final boolean isOwner = world.getOwnerDiscordId()
                .equals(userId);

        final boolean canWrite = world.getWritePermissions()
                .contains(userId);

        final boolean isAllowed = isOwner || canWrite;
        if (isPrivate && !isAllowed) {
            event.reply("You don't have permission from the owner of this private world to modify it")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private LorebookEntry updateEntry(final World world, final EventData eventData, final ModalInteractionEvent event) {

        final String eventAuthorId = event.getUser()
                .getId();

        final LorebookEntry oldEntry = eventData.getLorebookEntry();
        final String updatedEntryName = Optional.ofNullable(event.getValue(FIELD_NAME))
                .map(ModalMapping::getAsString)
                .orElse(oldEntry.getName());

        final String updatedEntryRegex = Optional.ofNullable(event.getValue(FIELD_REGEX))
                .map(ModalMapping::getAsString)
                .orElse(oldEntry.getRegex());

        final String updatedEntryDescription = Optional.ofNullable(event.getValue(FIELD_DESCRIPTION))
                .map(ModalMapping::getAsString)
                .orElse(oldEntry.getDescription());

        final String playerId = Optional
                .ofNullable(retrieveDiscordPlayerId(event.getValue(FIELD_PLAYER), event.getUser()
                        .getId()))
                .orElse(oldEntry.getPlayerDiscordId());

        final LorebookEntry entry = LorebookEntry.builder()
                .name(updatedEntryName)
                .description(updatedEntryDescription)
                .regex(updatedEntryRegex)
                .playerDiscordId(playerId)
                .build();

        return worldService.updateLorebookEntry(oldEntry.getId(), entry, eventAuthorId);
    }
}
