package es.thalesalv.chatrpg.application.service.commands;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.discord.listener.SessionListener;
import es.thalesalv.chatrpg.domain.enums.CommandHelpInfo;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;

@Service
@RequiredArgsConstructor
public class HelpCommandService implements DiscordCommand {

    private final SessionListener sessionListener;
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String OPTIONAL = "optional";
    private static final String REQUIRED = "required";
    private static final String EXAMPLES_INDENT = "\n\s\s\s\sExamples:";
    private static final String DESC_INDENT = "\n\s\s\s\s\s\s";
    private static final String COMMAND_DESCRIPTION = "\n/{0}: {1}";
    private static final String OPTION_DESCRIPTION = "\n\s\s({0}) {1}: {2}";
    private static final String HELP_COMMAND_TITLE = "This is the list of commands avaiable for {}. Some commands are within <> encapsulation to demonstrate that information of a specific type is needed, do not include <> brackets in the command.\n\n";
    private static final String UNKNOWN_ERROR = "An unknown error was caught while tokenizing string";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong running the command. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(HelpCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for help");
        try {
            event.deferReply();
            final String botName = event.getJDA()
                    .getSelfUser()
                    .getName();
            final List<String> commands = sessionListener.buildCommands()
                    .stream()
                    .map(cmd -> {
                        final StringBuilder sb = new StringBuilder(
                                MessageFormat.format(COMMAND_DESCRIPTION, cmd.getName(), cmd.getDescription()));
                        cmd.getOptions()
                                .forEach(opt -> {
                                    final String flag = opt.isRequired() ? REQUIRED : OPTIONAL;
                                    sb.append(MessageFormat.format(OPTION_DESCRIPTION, flag, opt.getName(),
                                            opt.getDescription()));
                                });
                        final List<String> cmds = CommandHelpInfo.findByCommandName(cmd.getName());
                        cmds.stream()
                                .findAny()
                                .map(a -> sb.append(EXAMPLES_INDENT));
                        cmds.forEach(desc -> {
                            sb.append(DESC_INDENT + desc);
                        });
                        return sb.toString()
                                .trim();
                    })
                    .collect(Collectors.toList());
            final String contentReply = HELP_COMMAND_TITLE.replace("{}", botName)
                    + String.join(StringUtils.LF, commands);
            final File file = File.createTempFile("bot-help-", ".txt");
            Files.write(file.toPath(), contentReply.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            final FileUpload fileUpload = FileUpload.fromData(file);
            event.replyFiles(fileUpload)
                    .setEphemeral(true)
                    .complete();
            fileUpload.close();
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
