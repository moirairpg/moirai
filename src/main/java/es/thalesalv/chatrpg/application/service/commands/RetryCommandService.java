package es.thalesalv.chatrpg.application.service.commands;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.EventDataMapper;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class RetryCommandService implements DiscordCommand {

    private static final String COMMAND_STRING = "retry";
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final EventDataMapper eventDataMapper;
    private static final String USE_CASE = "UseCase";
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String BOT_INSTRUCTION = " Simply react and respond to {0}''s message: {1}";
    private static final String NO_CONFIG_ATTACHED = "No configuration is attached to channel.";
    private static final String ERROR_OUTPUT_GENERATION = "Error regenerating output";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final String BOT_MESSAGE_NOT_FOUND = "No bot message found.";
    private static final String USER_MESSAGE_NOT_FOUND = "No user message found.";
    private static final Logger LOGGER = LoggerFactory.getLogger(RetryCommandService.class);

    @Override
    public void handleCommand(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for regeneration of message");
        try {
            event.deferReply();
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            final MessageChannelUnion channel = event.getChannel();
            channelRepository.findByChannelId(channel.getId())
                    .map(channelEntityToDTO)
                    .map(ch -> {
                        final Persona persona = ch.getChannelConfig()
                                .getPersona();
                        final ModelSettings modelSettings = ch.getChannelConfig()
                                .getSettings()
                                .getModelSettings();
                        final Message botMessage = retrieveBotMessage(channel, modelSettings, bot, persona);
                        final String completionType = AIModel.findByInternalName(modelSettings.getModelName())
                                .getCompletionType();
                        final EventData eventData = eventDataMapper.translate(bot, channel, ch, botMessage);
                        final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                        final BotUseCase useCase = (BotUseCase) applicationContext
                                .getBean(persona.getIntent() + USE_CASE);
                        event.reply("Re-generating output...")
                                .setEphemeral(true)
                                .queue(a -> a.deleteOriginal()
                                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        botMessage.delete()
                                .complete();
                        useCase.generateResponse(eventData, model);
                        return ch;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.debug(NO_CONFIG_ATTACHED);
            event.reply(NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_OUTPUT_GENERATION, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private String formatInput(String intent, Message message, SelfUser bot) {

        final String authorName = message.getAuthor()
                .getName();
        final String msgContent = message.getContentDisplay();
        final String formattedContent = MessageFormat.format(BOT_INSTRUCTION, authorName, msgContent);
        return "rpg".equals(intent) ? bot.getAsMention() + formattedContent : formattedContent;
    }

    private Message retrieveLastMessage(final MessageChannelUnion channel, final Message botMessage) {

        return channel.getHistoryBefore(botMessage, 1)
                .complete()
                .getRetrievedHistory()
                .stream()
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException(USER_MESSAGE_NOT_FOUND));
    }

    private Message retrieveBotMessage(final MessageChannelUnion channel, final ModelSettings modelSettings,
            final SelfUser bot, final Persona persona) {

        return channel.getHistory()
                .retrievePast(modelSettings.getChatHistoryMemory())
                .complete()
                .stream()
                .filter(m -> m.getAuthor()
                        .getId()
                        .equals(bot.getId()))
                .findFirst()
                .map(msg -> {
                    final Message lastMessage = retrieveLastMessage(channel, msg);
                    final String originalContent = formatInput(persona.getIntent(), lastMessage, channel.getJDA()
                            .getSelfUser());
                    msg.editMessage(originalContent)
                            .complete();
                    return msg;
                })
                .orElseThrow(() -> new IndexOutOfBoundsException(BOT_MESSAGE_NOT_FOUND));
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for message retry");
        return Commands.slash(COMMAND_STRING,
                "Deletes the last generated message and generates a new one in response to the latest chat message.");
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
