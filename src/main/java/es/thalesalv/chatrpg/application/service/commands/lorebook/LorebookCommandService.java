package es.thalesalv.chatrpg.application.service.commands.lorebook;

import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
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
public class LorebookCommandService implements DiscordCommand {

    private static final String USER_ACTION_NOT_FOUND = "User tried an action that does not exist";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookCommandService.class);
    private static final String COMMAND_STRING = "lb";

    private final LorebookCreateHandler createHandler;
    private final LorebookDeleteHandler deleteHandler;
    private final LorebookEditHandler editHandler;
    private final LorebookGetHandler getHandler;
    private final LorebookListHandler listHandler;

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling " + COMMAND_STRING + " command");
        final String commandName = Optional.ofNullable(event.getOption("action"))
                .map(OptionMapping::getAsString)
                .orElse(StringUtils.EMPTY);
        switch (commandName) {
            case "get" -> getHandler.handleCommand(event);
            case "set" -> createHandler.handleCommand(event);
            case "delete" -> deleteHandler.handleCommand(event);
            case "edit" -> editHandler.handleCommand(event);
            case "list" -> listHandler.handleCommand(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public void handleModal(ModalInteractionEvent event) {

        LOGGER.debug("handling " + COMMAND_STRING + " modal");
        final String modalId = event.getModalId();
        final String commandName = modalId.split("-")[0];
        switch (commandName) {
            case "set" -> createHandler.handleModal(event);
            case "delete" -> deleteHandler.handleModal(event);
            case "edit" -> editHandler.handleModal(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for lorebook operations");
        return Commands.slash(COMMAND_STRING,
                "Used with subcommands for management of lorebook entries belonging to the current channel's world.")
                .addOption(OptionType.STRING, "action", "One of the following: create, list, get, edit, delete.", true)
                .addOption(OptionType.STRING, "id", "ID of the entry to be managed. Usable for delete, edit and get.",
                        false);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
