package es.thalesalv.chatrpg.adapters.discord.listener;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

@Service
@RequiredArgsConstructor
public class SessionListener {

    @Value("${chatrpg.discord.status-channel-id}")
    private String statusChannelId;

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionListener.class);

    public void onReady(ReadyEvent event) {

        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            LOGGER.info("{} is ready to chat!", bot.getName());
            event.getJDA().updateCommands()
                    .addCommands(buildCommands())
                    .complete();

            Optional.ofNullable(statusChannelId)
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(id -> event.getJDA().getChannelById(TextChannel.class, id)
                            .sendMessage(bot.getName() + " is ready to chat!").complete());
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
            LOGGER.error("Error during disconnect", e);
        }
    }

    public void onShutdown(ShutdownEvent event) {

        try {
            final SelfUser bot = event.getJDA().getSelfUser();
            LOGGER.info("{} is shutdown.", bot.getName());
        } catch (Exception e) {
            LOGGER.error("Error during shutdown", e);
        }
    }

    public List<SlashCommandData> buildCommands() {

        return Arrays.asList(new SlashCommandData[] {
            registerHelpCommands(),
            registerStartCommand(),
            registerRetryCommand(),
            registerPromptCommand(),
            registerLorebookCommand(),
            registerEditCommand(),
            registerSetCommand(),
            registerUnsetCommand(),
            registerTokenizationCommand(),
            registerWorldCommand(),
            registerChConfCommand()
        });
    }

    private SlashCommandData registerHelpCommands() {

        LOGGER.debug("Registering help command");
        return Commands.slash("help", "Shows available commands and how to use them.");
    }

    private SlashCommandData registerLorebookCommand() {

        LOGGER.debug("Registering slash command for lorebook operations");
        return Commands.slash("lb", "Used with subcommands for management of lorebook entries belonging to the current channel's world.")
                .addOption(OptionType.STRING, "action", "One of the following: create, list, get, edit, delete.", true)
                .addOption(OptionType.STRING, "id", "ID of the entry to be managed. Usable for delete, edit and get.", false);
    }

    private SlashCommandData registerEditCommand() {

        LOGGER.debug("Registering slash command for message editing");
        return Commands.slash("edit", "Edits either the last message or a specified message from the bot if a message ID.")
                .addOption(OptionType.STRING, "message-id", "ID of the message to be edited", false);
    }

    private SlashCommandData registerRetryCommand() {

        LOGGER.debug("Registering slash command for message retry");
        return Commands.slash("retry", "Deletes the last generated message and generates a new one in response to the latest chat message.");
    }

    private SlashCommandData registerPromptCommand() {

        LOGGER.debug("Registering slash command for bot prompt");
        return Commands.slash("prompt", "Prompts as the bot's persona and allows for a generation in addition to the provided prompt.");
    }

    private SlashCommandData registerSetCommand() {

        LOGGER.debug("Registering slash command for setting definitions");
        return Commands.slash("set", "Sets the channel 'configuration' or 'world' to be associated with a specific channel.")
                .addOption(OptionType.STRING, "operation", "Choose what will be 'set': world, channel.", true)
                .addOption(OptionType.STRING, "id", "ID of the world/config to be set to this channel.", true);
    }

    private SlashCommandData registerUnsetCommand() {

        LOGGER.debug("Registering slash command for unsetting definitions");
        return Commands.slash("unset", "Removes channel config or world attached to a channel.")
                .addOption(OptionType.STRING, "operation", "Choose what will be unset: world, channel.", true);
    }

    private SlashCommandData registerTokenizationCommand() {

        LOGGER.debug("Registering slash command for tokenization");
        return Commands.slash("tk", "Tokenizes and counts tokens for the provided text.")
                .addOption(OptionType.STRING, "text", "Text that will be tokenized.", true);
    }

    private SlashCommandData registerStartCommand() {

        LOGGER.debug("Registering slash command for starting world");
        return Commands.slash("start", "Posts the default prompt for the current world into the chat and generates content for that world.");
    }

    private SlashCommandData registerWorldCommand() {

        LOGGER.debug("Registering slash command for world retrieval");
        return Commands.slash("wd", "Used with subcommands for management of the current channel's world.")
                .addOption(OptionType.STRING, "action", "One of the following: get, list.", true);
    }

    private SlashCommandData registerChConfCommand() {

        LOGGER.debug("Registering slash command for channel config retrieval");
        return Commands.slash("chconf", "Used with subcommands for management of the current channel's configuration.")
                .addOption(OptionType.STRING, "action", "One of the following: get, list.", true);
    }
}
