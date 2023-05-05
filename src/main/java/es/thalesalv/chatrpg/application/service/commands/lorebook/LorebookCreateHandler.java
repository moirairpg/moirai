package es.thalesalv.chatrpg.application.service.commands.lorebook;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.LorebookService;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.Lorebook;
import es.thalesalv.chatrpg.domain.model.chconf.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@Transactional
@RequiredArgsConstructor
public class LorebookCreateHandler {

    private final ContextDatastore contextDatastore;
    private final ObjectWriter prettyPrintObjectMapper;

    private final LorebookService lorebookService;
    private final ModerationService moderationService;
    private final ChannelRepository channelRepository;
    private final ChannelEntityToDTO channelEntityToDTO;

    private static final String MODAL_ID = "lb-create";
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String COMMAND_WRONG_CHANNEL = "This command cannot be issued from this channel.";
    private static final String ERROR_CREATING_LORE_ENTRY = "An error occurred while creating lore entry";
    private static final String ERROR_CREATE = "There was an error parsing your request. Please try again.";
    private static final String LORE_ENTRY_CREATED = "Lore entry with name **{0}** created. Don''t forget to save this ID!\n```json\n{1}\n```";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookCreateHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for lore entry creation");
        channelRepository.findById(event.getChannel()
                .getId())
                .map(channelEntityToDTO)
                .ifPresentOrElse(channel -> {
                    final World world = channel.getChannelConfig()
                            .getWorld();

                    checkPermissions(world, event);
                    saveEventDataToContext(channel, event.getChannel());
                    final Modal modal = buildEntryCreationModal();
                    event.replyModal(modal)
                            .queue();
                }, () -> event.reply(COMMAND_WRONG_CHANNEL)
                        .setEphemeral(true)
                        .queue(reply -> reply.deleteOriginal()
                                .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
    }

    public void handleModal(final ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received data from lore entry creation modal -> {}", event.getValues());
            event.deferReply();
            final EventData eventData = contextDatastore.getEventData();
            final Lorebook lorebook = eventData.getChannelDefinitions()
                    .getChannelConfig()
                    .getWorld()
                    .getLorebook();

            final LorebookEntry builtEntry = buildEntry(event);
            final String eventAuthorId = event.getUser()
                    .getId();

            final LorebookEntry insertedEntry = lorebookService.saveLorebookEntry(builtEntry, lorebook.getId(),
                    eventAuthorId);

            final String loreEntryJson = prettyPrintObjectMapper.writeValueAsString(insertedEntry);

            moderationService.moderate(loreEntryJson, contextDatastore.getEventData(), event)
                    .subscribe(response -> event
                            .reply(MessageFormat.format(LORE_ENTRY_CREATED, insertedEntry.getName(), loreEntryJson))
                            .setEphemeral(true)
                            .queue(m -> m.deleteOriginal()
                                    .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS)));
        } catch (Exception e) {
            LOGGER.error(ERROR_CREATING_LORE_ENTRY, e);
            event.reply(ERROR_CREATE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private Modal buildEntryCreationModal() {

        LOGGER.debug("Building entry creation modal");
        final TextInput lorebookEntryName = TextInput.create("lb-entry-name", "Name", TextInputStyle.SHORT)
                .setPlaceholder("Forest of the Talking Trees")
                .setRequired(true)
                .build();

        final TextInput lorebookEntryRegex = TextInput
                .create("lb-entry-regex", "Regular expression (optional)", TextInputStyle.SHORT)
                .setPlaceholder("/(Rain|)Forest of the (Talking|Speaking) Trees/gi")
                .setRequired(false)
                .build();

        final TextInput lorebookEntryDescription = TextInput
                .create("lb-entry-desc", "Description", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setRequired(true)
                .build();

        final TextInput lorebookEntryPlayer = TextInput
                .create("lb-entry-player", "Is this a player character?", TextInputStyle.SHORT)
                .setPlaceholder("y or n")
                .setMaxLength(1)
                .setRequired(true)
                .build();

        return Modal.create(MODAL_ID, "Lorebook Entry Creation")
                .addComponents(ActionRow.of(lorebookEntryName), ActionRow.of(lorebookEntryRegex),
                        ActionRow.of(lorebookEntryDescription), ActionRow.of(lorebookEntryPlayer))
                .build();
    }

    private void saveEventDataToContext(final Channel channelConfig, final MessageChannelUnion channel) {

        contextDatastore.setEventData(EventData.builder()
                .channelDefinitions(channelConfig)
                .currentChannel(channel)
                .build());
    }

    private void checkPermissions(World world, SlashCommandInteractionEvent event) {

        final Lorebook lorebook = world.getLorebook();
        final String userId = event.getUser()
                .getId();

        final boolean isPrivate = lorebook.getVisibility()
                .equals("private");

        final boolean isOwner = lorebook.getOwner()
                .equals(userId);

        final boolean canWrite = lorebook.getWritePermissions()
                .contains(userId);

        final boolean isAllowed = isOwner || canWrite;
        if (isPrivate && !isAllowed) {
            event.reply("You don't have permission from the owner of this private lorebook to see it")
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private LorebookEntry buildEntry(ModalInteractionEvent event) {

        final String entryName = event.getValue("lb-entry-name")
                .getAsString();

        final String entryRegex = event.getValue("lb-entry-regex")
                .getAsString();

        final String entryDescription = event.getValue("lb-entry-desc")
                .getAsString();

        final String entryPlayerCharacter = event.getValue("lb-entry-player")
                .getAsString();

        final boolean isPlayerCharacter = entryPlayerCharacter.equals("y");

        final User author = event.getMember()
                .getUser();

        return LorebookEntry.builder()
                .name(entryName)
                .description(entryDescription)
                .regex(entryRegex)
                .playerDiscordId(Optional.of(author.getId())
                        .filter(a -> isPlayerCharacter)
                        .orElse(StringUtils.EMPTY))
                .build();
    }
}
