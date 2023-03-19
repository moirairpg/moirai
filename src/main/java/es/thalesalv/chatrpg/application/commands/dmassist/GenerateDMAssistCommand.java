package es.thalesalv.chatrpg.application.commands.dmassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.completion.TextCompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.model.openai.dto.CommandEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
@RequiredArgsConstructor
public class GenerateDMAssistCommand extends DiscordCommand {

    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final ChannelEntityListToDTOList channelEntityListToDTOList;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String ERROR_EDITING = "Error editing message";
    private static final String MODEL_SERVICE = "ModelService";
    private static final String USE_CASE = "UseCase";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when generating the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateDMAssistCommand.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for assisted generation of message");

        try {
            event.deferReply();
            final MessageChannelUnion channel = event.getChannel();
            channel.sendTyping().complete();
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                .findFirst()
                .ifPresent(ch -> {
                    final Persona persona = ch.getChannelConfig().getPersona();
                    final ModelSettings modelSettings = ch.getChannelConfig().getSettings().getModelSettings();

                    channel.getHistory().retrievePast(1).complete().stream()
                        .findAny()
                        .map(message -> {
                            final MessageEventData messageEventData = messageEventDataTranslator.translate(event.getJDA().getSelfUser(), channel, ch.getChannelConfig(), message);
                            final TextCompletionService model = (TextCompletionService) applicationContext.getBean(modelSettings.getModelFamily() + MODEL_SERVICE);
                            final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                            final MessageEventData responseEventData = useCase.generateResponse(messageEventData, model);

                            contextDatastore.setCommandEventData(CommandEventData.builder()
                                    .messageToBeEdited(responseEventData.getResponseMessage())
                                    .channelConfig(ch.getChannelConfig())
                                    .channel(channel)
                                    .build());

                            event.replyModal(buildEditMessageModal(responseEventData.getResponseMessage())).queue();
                            return message;
                        })
                        .orElseThrow(() -> new IllegalStateException("No message history found"));
                });
           } catch (Exception e) {
            LOGGER.error("Error regenerating output", e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        LOGGER.debug("Received data of edit message for assisted generation modal");
        try {
            event.deferReply();
            final String messageContent = event.getValue("message-content").getAsString();
            final CommandEventData eventData = contextDatastore.getCommandEventData();
            moderationService.moderate(messageContent, eventData, event)
                    .subscribe(response -> {
                        eventData.getMessageToBeEdited().editMessage(messageContent).complete();
                        event.reply("New message generated").setEphemeral(true)
                                .complete().deleteOriginal().complete();
                    });
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
