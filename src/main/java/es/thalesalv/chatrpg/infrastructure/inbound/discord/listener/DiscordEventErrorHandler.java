package es.thalesalv.chatrpg.infrastructure.inbound.discord.listener;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.common.exception.ModerationException;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordEventErrorHandler {

    private final DiscordChannelPort discordChannelPort;

    private static final String MODERATION_INPUT_ERROR_TOPICS = "The content sent has been flagged by moderation. Please clear story of offensive topics.\nTopics flagged: %s.\n\nThis message will be deleted automatically in 20 seconds.";

    public void handle(Throwable exception) {

        if (exception instanceof ModerationException) {
            ModerationException moderationException = (ModerationException) exception;
            String topicsFlagged = String.join(", ", moderationException.getFlaggedTopics());
            discordChannelPort.sendTemporaryMessage(moderationException.getChannelId(),
                    String.format(MODERATION_INPUT_ERROR_TOPICS, topicsFlagged), 20);

            return;
        }

        log.error("Unhandled exception caught", exception);
    }
}
