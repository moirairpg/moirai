package es.thalesalv.chatrpg.application.service.commands.dmassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.config.BotConfig;
import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.service.commands.lorebook.CommandService;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class RetryDMAssistService implements CommandService {

    private final BotConfig botConfig;
    private final ApplicationContext applicationContext;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String MODEL_SERVICE = "ModelService";
    private static final String USE_CASE = "UseCase";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryDMAssistService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for regeneration of message");

        try {
            event.deferReply();
            botConfig.getPersonas().forEach(persona -> {
                final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> event.getChannel().getId().equals(id));
                if (isCurrentChannel) {
                    final MessageChannelUnion channel = event.getChannel();
                    final Message botMessage = channel.retrieveMessageById(channel.getLatestMessageId()).complete();
                    final Message userMessage = channel.getHistoryBefore(botMessage, 1).complete().getRetrievedHistory().get(0);
                    final MessageEventData messageEventData = messageEventDataTranslator.translate(event, persona, userMessage);
                    final GptModelService model = (GptModelService) applicationContext.getBean(persona.getModelFamily() + MODEL_SERVICE);
                    final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);

                    event.reply("Re-generating output...").setEphemeral(true).complete();
                    botMessage.delete().complete();
                    useCase.generateResponse(messageEventData, model);
                }
            });
        } catch (Exception e) {
            LOGGER.error("Error regenerating output", e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    @Override
    public void handle(ModalInteractionEvent event) {

        throw new UnsupportedOperationException("Regeneration of outputs doesn't have modals implemented");
    }
}
