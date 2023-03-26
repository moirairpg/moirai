package es.thalesalv.chatrpg.application.service.commands.dmassist;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.EventDataMapper;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.ModerationService;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.util.ContextDatastore;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.Channel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Service
@RequiredArgsConstructor
public class GenerateDMAssistCommandService implements DiscordCommand {

    private final ContextDatastore contextDatastore;
    private final ModerationService moderationService;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final ChannelEntityToDTO channelEntityMapper;
    private final EventDataMapper eventDataMapper;

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final String USE_CASE = "UseCase";
    private static final String ERROR_EDITING = "Error editing message";
    private static final String ERROR_OUTPUT_GENERATION = "Error generating output";
    private static final String NO_MESSAGE_HISTORY_FOUND = "No message history found";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when generating the message. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateDMAssistCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for assisted generation of message");
        try {
            event.deferReply();
            final MessageChannelUnion channel = event.getChannel();
            channel.sendTyping().complete();
            channelRepository.findByChannelId(event.getChannel().getId()).stream()
                    .findFirst()
                    .map(channelEntityMapper)
                    .ifPresent(ch -> {
                        final ChannelConfig channelConfig = ch.getChannelConfig();
                        final Persona persona = channelConfig.getPersona();
                        final ModelSettings modelSettings = channelConfig.getSettings().getModelSettings();

                        channel.getHistory().retrievePast(1).complete().stream()
                            .findAny()
                            .map(message -> {
                                final String completionType = AIModel.findByInternalName(modelSettings.getModelName()).getCompletionType();
                                final EventData eventData = eventDataMapper.translate(event.getJDA().getSelfUser(), channel, ch, message);
                                final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                                final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                                final EventData responseEventData = useCase.generateResponse(eventData, model);

                                saveEventDataToContext(responseEventData, ch, channel);
                                event.replyModal(buildEditMessageModal(responseEventData.getResponseMessage())).queue();
                                return message;
                            })
                            .orElseThrow(() -> new IllegalStateException(NO_MESSAGE_HISTORY_FOUND));
                    });
           } catch (Exception e) {
            LOGGER.error(ERROR_OUTPUT_GENERATION, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        }
    }

    @Override
    public void handle(final ModalInteractionEvent event) {

        LOGGER.debug("Received data of edit message for assisted generation modal");
        try {
            event.deferReply();
            final String messageContent = event.getValue("message-content").getAsString();
            final EventData eventData = contextDatastore.getEventData();
            moderationService.moderate(messageContent, eventData, event)
                    .subscribe(response -> {
                        eventData.getMessageToBeEdited().editMessage(messageContent).complete();
                        event.reply("New message generated").setEphemeral(true)
                                .queue(a -> a.deleteOriginal().queueAfter(1, TimeUnit.MILLISECONDS));
                    });
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        }
    }

    private Modal buildEditMessageModal(final Message msg) {

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

    private void saveEventDataToContext(final EventData responseEventData, final Channel channelConfig, final MessageChannelUnion channel) {

        contextDatastore.setEventData(EventData.builder()
                .messageToBeEdited(responseEventData.getResponseMessage())
                .channelDefinitions(channelConfig)
                .currentChannel(channel)
                .build());
    }
}
