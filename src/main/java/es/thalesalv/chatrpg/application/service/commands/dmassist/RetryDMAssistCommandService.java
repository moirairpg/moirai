package es.thalesalv.chatrpg.application.service.commands.dmassist;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class RetryDMAssistCommandService implements DiscordCommand {

    private final ChannelEntityToDTO channelEntityMapper;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final MessageEventDataTranslator eventDataTranslator;

    private static final String USE_CASE = "UseCase";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final String BOT_MESSAGE_NOT_FOUND = "No bot message found.";
    private static final String USER_MESSAGE_NOT_FOUND = "No user message found.";

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryDMAssistCommandService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for regeneration of message");

        try {
            event.deferReply();
            final SelfUser bot = event.getJDA().getSelfUser();
            final MessageChannelUnion channel = event.getChannel();
            channelRepository.findByChannelId(event.getChannel().getId()).stream()
                    .findFirst()
                    .map(channelEntityMapper)
                    .ifPresent(ch -> {
                        final Persona persona = ch.getChannelConfig().getPersona();
                        final ModelSettings modelSettings = ch.getChannelConfig().getSettings().getModelSettings();
                        final Message botMessage = retrieveBotMessage(channel, modelSettings, bot);
                        final Message userMessage = retrieveUserMessage(channel, botMessage);

                        final String completionType = AIModel.findByInternalName(modelSettings.getModelName()).getCompletionType();
                        final EventData eventData = eventDataTranslator.translate(bot, channel, ch, userMessage);
                        final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                        final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);

                        event.reply("Re-generating output...")
                                .setEphemeral(true).queue(a -> a.deleteOriginal().queueAfter(20, TimeUnit.SECONDS));

                        botMessage.delete().complete();
                        useCase.generateResponse(eventData, model);
                    });
        } catch (Exception e) {
            LOGGER.error("Error regenerating output", e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }

    private Message retrieveUserMessage(final MessageChannelUnion channel, final Message botMessage) {

        return channel.getHistoryBefore(botMessage, 1).complete().getRetrievedHistory().stream()
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException(USER_MESSAGE_NOT_FOUND));
    }

    private Message retrieveBotMessage(final MessageChannelUnion channel, final ModelSettings modelSettings, final SelfUser bot) {

        return channel.getHistory().retrievePast(modelSettings.getChatHistoryMemory()).complete().stream()
                .filter(m -> m.getAuthor().getId().equals(bot.getId()))
                .findFirst()
                .orElseThrow(() -> new IndexOutOfBoundsException(BOT_MESSAGE_NOT_FOUND));
    }
}
