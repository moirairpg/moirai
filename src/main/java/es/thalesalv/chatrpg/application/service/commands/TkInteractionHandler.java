package es.thalesalv.chatrpg.application.service.commands;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.discord.listener.InteractionListener;
import es.thalesalv.chatrpg.application.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class TkInteractionHandler implements DiscordInteractionHandler {

    private static final String COMMAND_STRING = "tk";
    private static final String TEXT_OPTION = "text";
    private final TokenizerService tokenizerService;
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionListener.class);
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String TOKEN_REPLY_MESSAGE = "**Tokens:** {0} (contains {1} total tokens).";
    private static final String UNKNOWN_ERROR = "An unknown error was caught while tokenizing string";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when tokenizing the text. Please try again.";

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling {} command", COMMAND_STRING);
        try {
            final String text = event.getOption(TEXT_OPTION)
                    .getAsString();
            final String tokens = tokenizerService.tokenize(text);
            final int tokenCount = tokenizerService.countTokens(text);
            event.reply(MessageFormat.format(TOKEN_REPLY_MESSAGE, tokens, tokenCount))
                    .setEphemeral(true)
                    .queue();
        } catch (Exception e) {
            LOGGER.error(UNKNOWN_ERROR, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for tokenization");
        return Commands.slash(COMMAND_STRING, "Tokenizes and counts tokens for the provided text.")
                .addOption(OptionType.STRING, TEXT_OPTION, "Text that will be tokenized.", true);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }

}
