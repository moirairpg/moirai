package es.thalesalv.chatrpg.application.service.commands;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.discord.listener.InteractionListener;
import es.thalesalv.chatrpg.application.service.TokenizerService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@RequiredArgsConstructor
public class TkCommandService implements DiscordCommand {

    private final TokenizerService tokenizerService;

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionListener.class);

    private static final int DELETE_EPHEMERAL_TIMER = 20;

    private static final String TOKEN_REPLY_MESSAGE = "**Tokens:** {0} (contains {1} total tokens).";
    private static final String UNKNOWN_ERROR = "An unknown error was caught while tokenizing string";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when tokenizing the text. Please try again.";

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {

            LOGGER.debug("Received slash command for tokenization of strings");
            final String text = event.getOption("text")
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
}
