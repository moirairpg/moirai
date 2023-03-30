package es.thalesalv.chatrpg.application.service.commands.world;

import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorldCommandService implements DiscordCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldCommandService.class);
    private static final String USER_ACTION_NOT_FOUND = "User tried an action that does not exist";
    private static final String COMMAND_STRING = "wd";
    private final WorldGetHandler getHandler;
    private final WorldListHandler listHandler;
    private final WorldSetHandler setHandler;

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling " + COMMAND_STRING + " command");
        final String commandName = Optional.ofNullable(event.getOption("action"))
                .map(OptionMapping::getAsString)
                .orElse(StringUtils.EMPTY);
        switch (commandName) {
            case "get" -> getHandler.handleCommand(event);
            case "set" -> setHandler.handleCommand(event);
            case "list" -> listHandler.handleCommand(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for world retrieval");
        return Commands.slash(COMMAND_STRING, "Used with subcommands for management of the current channel's world.")
                .addOption(OptionType.STRING, "action", "One of the following: get, list.", true);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
