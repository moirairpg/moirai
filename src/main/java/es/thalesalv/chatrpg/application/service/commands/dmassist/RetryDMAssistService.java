package es.thalesalv.chatrpg.application.service.commands.dmassist;

import net.dv8tion.jda.api.entities.User;
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
import net.dv8tion.jda.api.interactions.InteractionHook;

@Service
@RequiredArgsConstructor
public class RetryDMAssistService implements CommandService {

    private final BotConfig botConfig;
    private final ApplicationContext applicationContext;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String MODEL_SERVICE = "ModelService";
    private static final String USE_CASE = "UseCase";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final String BOT_MESSAGE_NOT_FOUND = "No bot message found.";
    private static final String USER_MESSAGE_NOT_FOUND = "No user message found.";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryDMAssistService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for regeneration of message");

        try {
            event.deferReply();
            User bot = event.getJDA().getSelfUser();
            final MessageChannelUnion channel = event.getChannel();
            botConfig.getPersonas().stream().filter(persona -> persona.getChannelIds().contains(channel.getId())).findAny().ifPresent(persona -> {
                final Message botMessage = channel.getHistory().retrievePast(persona.getChatHistoryMemory()).complete().stream()
                        .filter(m -> m.getAuthor().getId().equals(bot.getId()))
                        .findFirst()
                        .orElseThrow(() -> new IndexOutOfBoundsException(BOT_MESSAGE_NOT_FOUND));
                final Message userMessage = channel.getHistoryBefore(botMessage, 1).complete().getRetrievedHistory().stream()
                        .findAny()
                        .orElseThrow(() -> new IndexOutOfBoundsException(USER_MESSAGE_NOT_FOUND));
                final MessageEventData messageEventData = messageEventDataTranslator.translate(event, persona, userMessage);
                final GptModelService model = (GptModelService) applicationContext.getBean(persona.getModelFamily() + MODEL_SERVICE);
                final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);

                final InteractionHook hook = event.reply("Re-generating output...").setEphemeral(true).complete();
                botMessage.delete().complete();
                useCase.generateResponse(messageEventData, model);
                hook.deleteOriginal().complete();
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
