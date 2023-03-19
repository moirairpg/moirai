package es.thalesalv.chatrpg.application.service.commands.dmassist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import es.thalesalv.chatrpg.application.translator.chconfig.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.domain.enums.AIModelEnum;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

@Service
@RequiredArgsConstructor
public class RetryDMAssistCommandService extends DiscordCommand {

    private final ChannelEntityListToDTOList channelEntityListToDTOList;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final MessageEventDataTranslator messageEventDataTranslator;

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
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                .findFirst()
                .ifPresent(ch -> {
                    final Persona persona = ch.getChannelConfig().getPersona();
                    final ModelSettings modelSettings = ch.getChannelConfig().getSettings().getModelSettings();
                    final Message botMessage = channel.getHistory().retrievePast(modelSettings.getChatHistoryMemory()).complete().stream()
                            .filter(m -> m.getAuthor().getId().equals(bot.getId()))
                            .findFirst()
                            .orElseThrow(() -> new IndexOutOfBoundsException(BOT_MESSAGE_NOT_FOUND));

                    final Message userMessage = channel.getHistoryBefore(botMessage, 1).complete().getRetrievedHistory().stream()
                            .findAny()
                            .orElseThrow(() -> new IndexOutOfBoundsException(USER_MESSAGE_NOT_FOUND));

                    final String completionType = AIModelEnum.findByInternalName(modelSettings.getModelName()).getCompletionType();
                    final MessageEventData messageEventData = messageEventDataTranslator.translate(bot, channel, ch.getChannelConfig(), userMessage);
                    final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
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
}
