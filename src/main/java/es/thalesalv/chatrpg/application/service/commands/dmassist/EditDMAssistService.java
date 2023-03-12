package es.thalesalv.chatrpg.application.service.commands.dmassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.ContextDatastore;
import es.thalesalv.chatrpg.application.config.BotConfig;
import es.thalesalv.chatrpg.application.config.CommandEventData;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.lorebook.CommandService;
import es.thalesalv.chatrpg.domain.exception.DiscordFunctionException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@RequiredArgsConstructor
public class EditDMAssistService implements CommandService {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;

    private static final Logger LOGGER = LoggerFactory.getLogger(EditDMAssistService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        event.deferReply();
        botConfig.getPersonas().forEach(persona -> {
            try {
                final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> event.getChannel().getId().equals(id));
                if (isCurrentChannel) {
                    final String messageId = event.getOption("message-id").getAsString();
                    event.getChannel().retrieveMessageById(messageId).submit()
                            .whenComplete((msg, error) -> {
                                if (error != null)
                                    throw new DiscordFunctionException("Failed to retrieve message for editing", error);

                                contextDatastore.setCommandEventData(CommandEventData.builder()
                                        .messageToBeEdited(msg)
                                        .build());

                                final Modal editMessageModal = buildEditMessageModal(msg);
                                event.replyModal(editMessageModal).queue();
                            });
                }
            } catch (Exception e) {
                LOGGER.error("Error editing message", e);
                event.reply("Something went wrong when editing the message. Please try again.")
                        .setEphemeral(true).queue();
            }
        });
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
                                throw new DiscordFunctionException("Failed to edit message for slash command", error);
        
                            event.reply("Message has been edited").setEphemeral(true).queue();
                        }));
        } catch (Exception e) {
            LOGGER.error("Error editing message", e);
            event.reply("Something went wrong when editing the message. Please try again.")
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

        return Modal.create("message-edit-modal", "Edit message content")
                .addComponents(ActionRow.of(messageContent)).build();
    }
}
