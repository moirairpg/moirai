package es.thalesalv.chatrpg.adapters.discord.listener;

import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Service
public class SessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    public void onReady(ReadyEvent event) {
        try {
            final SelfUser bot = event.getJDA().getSelfUser();
                LOGGER.info("{} is ready to chat!", bot.getName());
                registerSlashCommands(event);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Session is not yet ready!")) {
                LOGGER.warn("Waiting for Discord session...");
            } else {
                LOGGER.error("Error during event", e);
            }
        }
    }

    public void onSessionDisconnect(SessionDisconnectEvent event) {
        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            LOGGER.info("{} is disconnected.", bot.getName());
        } catch (Exception e) {
            LOGGER.error("Error during disconnect: ", e);
        }
    }

    public void onShutdown(ShutdownEvent event) {
        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            LOGGER.info("{} is shutdown.", bot.getName());
        } catch (Exception e) {
            LOGGER.error("Error during shutdown: ", e);
        }
    }

    private void registerSlashCommands(ReadyEvent event) {

        LOGGER.debug("Registering slash commands.");
        final JDA jda = event.getJDA();
        final SlashCommandData lorebook = Commands.slash("lorebook", "Manages lorebook entries.")
                .addOption(OptionType.STRING, "action", "One of the following: create, retrieve, update, delete", true)
                .addOption(OptionType.STRING, "lorebook-entry-id", "UUID of the entry to be managed. Usable for delete, update and retrieve.", false);

        jda.updateCommands().addCommands(lorebook).complete();
    }
}
