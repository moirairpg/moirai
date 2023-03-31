package es.thalesalv.chatrpg.application.service.commands;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import es.thalesalv.chatrpg.domain.enums.Intent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.StringUtils;
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
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.EventData;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.chconf.Persona;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class StartCommandService implements DiscordCommand {

    private static final String COMMAND_STRING = "start";
    private final ChannelEntityToDTO channelEntityToDTO;
    private final ApplicationContext applicationContext;
    private final ChannelRepository channelRepository;
    private final EventDataMapper eventDataMapper;
    private static final String USE_CASE = "UseCase";
    private static final int DELETE_EPHEMERAL_TIMER = 20;

    private static final String DEFAULT_PROMPT_NOT_FOUND = "World does not have a default prompt set";
    private static final String NO_CONFIG_ATTACHED = "No configuration is attached to channel.";
    private static final String UNKNOWN_ERROR = "An unknown error was caught while starting world";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(StartCommandService.class);

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        try {
            event.deferReply();
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            final MessageChannelUnion channel = event.getChannel();
            channelRepository.findByChannelId(channel.getId())
                    .map(channelEntityToDTO)
                    .map(ch -> {
                        final World world = ch.getChannelConfig()
                                .getWorld();
                        final Persona persona = ch.getChannelConfig()
                                .getPersona();
                        final ModelSettings modelSettings = ch.getChannelConfig()
                                .getSettings()
                                .getModelSettings();

                        final String initialPrompt = Optional.ofNullable(world.getInitialPrompt())
                                .orElseThrow(() -> new WorldNotFoundException(DEFAULT_PROMPT_NOT_FOUND));

                        final String input = formatInput(persona.getIntent(), initialPrompt, bot);
                        final Message message = channel.sendMessage(input)
                                .complete();
                        final String completionType = AIModel.findByInternalName(modelSettings.getModelName())
                                .getCompletionType();
                        final EventData eventData = eventDataMapper.translate(bot, channel, ch, message);
                        final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                        final BotUseCase useCase = (BotUseCase) applicationContext
                                .getBean(persona.getIntent() + USE_CASE);
                        event.reply("Starting world...")
                                .setEphemeral(true)
                                .queue(a -> a.deleteOriginal()
                                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        useCase.generateResponse(eventData, model);
                        message.editMessage(message.getContentRaw()
                                .replace(bot.getAsMention(), StringUtils.EMPTY)
                                .trim())
                                .complete();
                        return ch;
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (WorldNotFoundException e) {
            LOGGER.debug(e.getMessage());
            event.reply(e.getMessage())
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (ChannelConfigNotFoundException e) {
            LOGGER.debug(NO_CONFIG_ATTACHED);
            event.reply(NO_CONFIG_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private String formatInput(Intent intent, String prompt, SelfUser bot) {

        return Intent.RPG.equals(intent) ? bot.getAsMention() + prompt : prompt;
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for starting world");
        return Commands.slash(COMMAND_STRING,
                "Posts the default prompt for the current world into the chat and generates content for that world.");
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
