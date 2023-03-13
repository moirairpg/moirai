package es.thalesalv.chatrpg.application.service.commands.dmassist;

import es.thalesalv.chatrpg.adapters.data.ContextDatastore;
import es.thalesalv.chatrpg.application.config.BotConfig;
import es.thalesalv.chatrpg.application.config.CommandEventData;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.lorebook.CommandService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
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
public class EditDMAssistService implements CommandService {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final String ERROR_EDITING = "Error editing message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";

    private static final String BOT_NOT_FOUND = "No bot message found.";
    private static final Logger LOGGER = LoggerFactory.getLogger(EditDMAssistService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        try {
            event.deferReply();
            final SelfUser bot = event.getJDA().getSelfUser();
            botConfig.getPersonas().stream()
                    .filter(p -> p.getChannelIds().contains(event.getChannel().getId()))
                    .findFirst()
                    .ifPresent(persona -> {
                        Message message = Optional.ofNullable(event.getOption("message-id"))
                                .map(OptionMapping::getAsString)
                                .map(event.getChannel()::retrieveMessageById)
                                .map(RestAction::complete)
                                .orElseGet(() -> event.getChannel().getHistory().retrievePast(persona.getChatHistoryMemory()).complete().stream()
                                        .filter(a -> a.getAuthor().getId().equals(bot.getId()))
                                        .findFirst().orElseThrow(() -> new ArrayIndexOutOfBoundsException(BOT_NOT_FOUND)));
                        final Modal editMessageModal = buildEditMessageModal(message);
                        event.replyModal(editMessageModal).queue();
                        contextDatastore.setCommandEventData(CommandEventData.builder()
                                .messageToBeEdited(message)
                                .persona(persona)
                                .build());
                    });
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        LOGGER.debug("Received data of edit message modal");
        try {
            event.deferReply();
            final String messageContent = event.getValue("message-content").getAsString();
            final CommandEventData eventData = contextDatastore.getCommandEventData();
            final Message message = eventData.getMessageToBeEdited();
            moderationService.moderate(messageContent, eventData, event)
                    .subscribe(response -> message.editMessage(messageContent).submit()
                        .whenComplete((msg, error) -> {
                            if (error != null)
                                throw new DiscordFunctionException("Error in message edition modal", error);
        
                            event.reply("Message has been edited").setEphemeral(true)
                                    .queue(m -> m.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
                        }));
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    private Modal buildEditMessageModal(Message msg) {

        LOGGER.debug("Building message edition modal");
        final TextInput messageContent = TextInput
                .create("message-content", "Message content", TextInputStyle.PARAGRAPH)
                .setPlaceholder("The Forest of the Talking Trees is located in the west of the country.")
                .setValue(msg.getContentDisplay())
                .setMaxLength(2000)
                .setRequired(true)
                .build();

        return Modal.create("edit-message-dmassist-modal", "Edit message content")
                .addComponents(ActionRow.of(messageContent)).build();
    }
}
