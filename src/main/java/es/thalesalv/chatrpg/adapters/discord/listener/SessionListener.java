package es.thalesalv.chatrpg.adapters.discord.listener;

import es.thalesalv.chatrpg.application.config.BotConfig;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.SessionDisconnectEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SessionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);
    private final BotConfig botConfig;

    public void onReady(ReadyEvent event) {
        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            LOGGER.info("{} is ready to chat!", bot.getName());
            event.getJDA().updateCommands().addCommands(buildCommands(registerLorebookSlashCommands(),
                    registerDmAssistSlashCommands())).queue();
            Optional.ofNullable(botConfig.getStatusChannelId())
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(
                            id -> event.getJDA().getChannelById(TextChannel.class, id).sendMessage(bot.getName() + " is ready to chat!").complete()
                    );

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

    private List<SlashCommandData> buildCommands(SlashCommandData... commandsToAdd) {

        return Arrays.asList(commandsToAdd);
    }

    private SlashCommandData registerLorebookSlashCommands() {

        LOGGER.debug("Registering Lorebook slash commands.");
        return Commands.slash("lorebook", "Manages lorebook entries.")
                .addOption(OptionType.STRING, "action", "One of the following: create, retrieve, update, delete", true)
                .addOption(OptionType.STRING, "lorebook-entry-id", "UUID of the entry to be managed. Usable for delete, update and retrieve.", false);
    }

    private SlashCommandData registerDmAssistSlashCommands() {

        LOGGER.debug("Registering DM Assist slash commands.");
        return Commands.slash("dmassist", "Commands for Dungeon Master assistance.")
                .addOption(OptionType.STRING, "action", "One of the following: generate, edit, retry", true)
                .addOption(OptionType.STRING, "message-id", "ID of the message meant to be edited. Only appliable to the \"edit\" action.", false);
    }
}
