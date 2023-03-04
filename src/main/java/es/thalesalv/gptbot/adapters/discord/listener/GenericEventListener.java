package es.thalesalv.gptbot.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Service
public class GenericEventListener implements EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericEventListener.class);

    @Override
    public void onEvent(GenericEvent event) {

        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            if (event instanceof ReadyEvent) {
                LOGGER.info("{} is ready to chat!", bot.getName());
                registerSlashCommands(event);
            }
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("Session is not yet ready!")) {
                LOGGER.warn("Waiting for Discord session...");
            } else {
                LOGGER.error("Error during event", e);
            }
        }
    }

    private void registerSlashCommands(GenericEvent event) {

        LOGGER.debug("Registering slash commands.");
        final JDA jda = event.getJDA();
        final SlashCommandData lorebook = Commands.slash("lorebook", "Manages lorebook entries.")
                .addOption(OptionType.STRING, "action", "One of the following: create, retrieve, update, delete", true)
                .addOption(OptionType.STRING, "lorebook-entry-id", "UUID of the entry to be managed. Usable for delete, update and retrieve.", false);

        jda.updateCommands().addCommands(lorebook).complete();
    }
}
