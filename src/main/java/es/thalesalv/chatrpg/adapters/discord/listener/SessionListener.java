package es.thalesalv.chatrpg.adapters.discord.listener;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.service.commands.BotCommands;
import es.thalesalv.chatrpg.application.service.commands.HelpInteractionHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;

@Service
@RequiredArgsConstructor
public class SessionListener {

    @Value("${chatrpg.discord.status-channel-id}")
    private String statusChannelId;

    private final BotCommands commands;
    private final HelpInteractionHandler helpService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    public void onReady(ReadyEvent event) {

        try {
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            LOGGER.info("{} is ready to chat!", bot.getName());
            event.getJDA()
                    .updateCommands()
                    .addCommands(commands.listWith(helpService))
                    .complete();
            Optional.ofNullable(statusChannelId)
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(id -> event.getJDA()
                            .getChannelById(TextChannel.class, id)
                            .sendMessage(bot.getName() + " is ready to chat!")
                            .complete());
        } catch (IllegalStateException e) {
            if (e.getMessage()
                    .contains("Session is not yet ready!")) {
                LOGGER.warn("Waiting for Discord session...");
            } else {
                LOGGER.error("Error during event", e);
            }
        }
    }

    public void onSessionDisconnect(SessionDisconnectEvent event) {

        try {
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            LOGGER.info("{} is disconnected.", bot.getName());
        } catch (Exception e) {
            LOGGER.error("Error during disconnect", e);
        }
    }

    public void onShutdown(ShutdownEvent event) {

        try {
            final SelfUser bot = event.getJDA()
                    .getSelfUser();
            LOGGER.info("{} is shutdown.", bot.getName());
        } catch (Exception e) {
            LOGGER.error("Error during shutdown", e);
        }
    }
}
