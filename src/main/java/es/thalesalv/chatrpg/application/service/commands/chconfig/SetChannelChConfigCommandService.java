package es.thalesalv.chatrpg.application.service.commands.chconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class SetChannelChConfigCommandService extends DiscordCommand {

    private static final String ERROR_EDITING = "Error editing message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(SetChannelChConfigCommandService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        try {
            event.deferReply();

        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true).queue();
        }
    }
}
