package es.thalesalv.chatrpg.application.service.commands.world;

import es.thalesalv.chatrpg.application.service.commands.DiscordInteractionHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorldInteractionHandler implements DiscordInteractionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldInteractionHandler.class);
    private static final String USER_ACTION_NOT_FOUND = "User tried an action that does not exist";
    private static final String COMMAND_STRING = "wd";
    private static final String ACTION_OPTION = "action";
    private static final String ID_OPTION = "id";
    private final WorldGetHandler getHandler;
    private final WorldListHandler listHandler;
    private final WorldSetHandler setHandler;
    private final WorldUnsetHandler unsetHandler;

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling {} command", COMMAND_STRING);
        WorldAction action = Optional.ofNullable(event.getOption(ACTION_OPTION))
                .map(OptionMapping::getAsString)
                .flatMap(WorldAction::byName)
                .orElseThrow(() -> new RuntimeException(USER_ACTION_NOT_FOUND));
        switch (action) {
            case GET -> getHandler.handleCommand(event);
            case SET -> setHandler.handleCommand(event);
            case LIST -> listHandler.handleCommand(event);
            case UNSET -> unsetHandler.handleCommand(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for world retrieval");
        String actionDescription = MessageFormat.format("One of the following: {0}.", WorldAction.listAsString());
        return Commands.slash(COMMAND_STRING, "Used with subcommands for management of the current channel's world.")
                .addOption(OptionType.STRING, ACTION_OPTION, actionDescription, true)
                .addOption(OptionType.STRING, ID_OPTION, "The id of the world", false);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
