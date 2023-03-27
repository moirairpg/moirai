package es.thalesalv.chatrpg.application.service.commands;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.discord.listener.SessionListener;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

@Service
@RequiredArgsConstructor
public class HelpCommandService implements DiscordCommand {

    private final SessionListener sessionListener;

    private static final int DELETE_EPHEMERAL_TIMER = 20;

    private static final String COMMAND_DESCRIPTION = "\n**- {0}:** {1}";
    private static final String OPTION_DESCRIPTION = "**\n\t\t{0}:** {1}";
    private static final String HELP_COMMAND_TITLE = "This is the list of commands avaiable for {}. If you still have issues using the bot, please speak to an administrator.\n\n";
    private static final String UNKNOWN_ERROR = "An unknown error was caught while tokenizing string";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong running the command. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for help");
        try {
            event.deferReply();
            final String botName = event.getJDA().getSelfUser().getName();
            final List<String> commands = sessionListener.buildCommands().stream()
                    .map(cmd -> {
                        String commandDetails = MessageFormat.format(COMMAND_DESCRIPTION,
                                cmd.getName(), cmd.getDescription());
                        for (OptionData opt : cmd.getOptions()) {
                            commandDetails += MessageFormat.format(OPTION_DESCRIPTION,
                                    opt.getName(), opt.getDescription());
                        }

                        return commandDetails.trim();
                    })
                    .collect(Collectors.toList());

            event.reply(HELP_COMMAND_TITLE.replace("{}", botName)
                    + String.join(StringUtils.LF, commands)).setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true)
                    .queue(m -> m.deleteOriginal().queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
