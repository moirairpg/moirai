package es.thalesalv.chatrpg.adapters.discord.listener;

import es.thalesalv.chatrpg.application.service.BotCommands;
import es.thalesalv.chatrpg.application.service.commands.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionListener {

    private final BotCommands commands;
    private final HelpCommandService helpService;

    @Value("${chatrpg.discord.status-channel-id}")
    private final String statusChannelId;
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
