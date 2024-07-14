package es.thalesalv.chatrpg.infrastructure.inbound.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.common.exception.ModerationException;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;

@Component
public class DiscordEventErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DiscordEventErrorHandler.class);

    private final DiscordChannelPort discordChannelPort;

    private static final String MODERATION_INPUT_ERROR_TOPICS = "The content sent has been flagged by moderation. Please clear story of offensive topics.\nTopics flagged: %s.\n\nThis message will be deleted automatically in 20 seconds.";

    public DiscordEventErrorHandler(DiscordChannelPort discordChannelPort) {
        this.discordChannelPort = discordChannelPort;
    }

    public void handle(Throwable exception) {

        if (exception instanceof ModerationException) {
            ModerationException moderationException = (ModerationException) exception;
            String topicsFlagged = String.join(", ", moderationException.getFlaggedTopics());
            discordChannelPort.sendTemporaryMessage(moderationException.getChannelId(),
                    String.format(MODERATION_INPUT_ERROR_TOPICS, topicsFlagged), 20);

            return;
        }

        LOG.error("Unhandled exception caught", exception);
    }
}
