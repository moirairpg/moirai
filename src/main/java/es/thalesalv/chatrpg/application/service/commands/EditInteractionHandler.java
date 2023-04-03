package es.thalesalv.chatrpg.application.service.commands;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.moderation.ModerationService;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.RestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EditInteractionHandler implements DiscordInteractionHandler {

    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final ChannelRepository channelRepository;
    private final ChannelEntityToDTO channelEntityToDTO;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String COMMAND_STRING = "edit";
    private static final String ID_OPTION = "id";
    private static final String MODAL_MESSAGE_CONTENT = "message-content";
    private static final String NO_CONFIG_ATTACHED = "No configuration is attached to channel.";
    private static final String ERROR_EDITING = "Error editing message";
    private static final String BOT_NOT_FOUND = "No bot message found.";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(EditInteractionHandler.class);

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling {} command", COMMAND_STRING);
        try {
            event.deferReply();
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            channelRepository.findById(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {
                        final ModelSettings modelSettings = channel.getChannelConfig()
                                .getSettings()
                                .getModelSettings();
                        final Message message = retrieveMessageToBeEdited(event, modelSettings, bot);
                        final Modal editMessageModal = buildEditMessageModal(message);
                        saveEventDataToContext(message, channel);
                        event.replyModal(editMessageModal)
                                .queue();
                        return channel;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.debug(NO_CONFIG_ATTACHED);
            event.reply(NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    @Override
    public void handleModal(final ModalInteractionEvent event) {

        LOGGER.debug("handling {} modal", COMMAND_STRING);
        try {
            event.deferReply();
            final String messageContent = event.getValue(MODAL_MESSAGE_CONTENT)
                    .getAsString();
            final EventData eventData = contextDatastore.getEventData();
            final Message message = eventData.getMessageToBeEdited();
            moderationService.moderate(messageContent, eventData, event)
                    .subscribe(response -> message.editMessage(messageContent)
                            .submit()
                            .whenComplete((msg, error) -> {
                                if (error != null)
                                    throw new DiscordFunctionException("Error in message edition modal", error);
                                event.reply("Message has been edited")
                                        .setEphemeral(true)
                                        .queue(m -> m.deleteOriginal()
                                                .queueAfter(1, TimeUnit.MILLISECONDS));
                            }));
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private Modal buildEditMessageModal(final Message msg) {

        LOGGER.debug("Building message edition modal");
        final TextInput messageContent = TextInput
                .create(MODAL_MESSAGE_CONTENT, "Message content", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setValue(msg.getContentDisplay())
                .setMaxLength(2000)
                .setRequired(true)
                .build();
        return Modal.create(COMMAND_STRING, "Edit message content")
                .addComponents(ActionRow.of(messageContent))
                .build();
    }

    private Message retrieveMessageToBeEdited(final SlashCommandInteractionEvent event,
            final ModelSettings modelSettings, final SelfUser bot) {

        return Optional.ofNullable(event.getOption(ID_OPTION))
                .map(OptionMapping::getAsString)
                .map(event.getChannel()::retrieveMessageById)
                .map(RestAction::complete)
                .orElseGet(() -> event.getChannel()
                        .getHistory()
                        .retrievePast(modelSettings.getChatHistoryMemory())
                        .complete()
                        .stream()
                        .filter(a -> a.getAuthor()
                                .getId()
                                .equals(bot.getId()))
                        .findFirst()
                        .orElseThrow(() -> new ArrayIndexOutOfBoundsException(BOT_NOT_FOUND)));
    }

    private void saveEventDataToContext(final Message message, final Channel channel) {

        contextDatastore.setEventData(EventData.builder()
                .messageToBeEdited(message)
                .channelDefinitions(channel)
                .build());
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for message editing");
        return Commands
                .slash(COMMAND_STRING,
                        "Edits either the last message or a specified message from the bot if a message ID.")
                .addOption(OptionType.STRING, ID_OPTION, "ID of the message to be edited", false);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
