package es.thalesalv.chatrpg.application.service.commands.lorebook;

import es.thalesalv.chatrpg.application.service.commands.DiscordInteractionHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
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
public class LorebookInteractionHandler implements DiscordInteractionHandler {

    private static final String USER_ACTION_NOT_FOUND = "User tried an action that does not exist";
    private static final Logger LOGGER = LoggerFactory.getLogger(LorebookInteractionHandler.class);
    private static final String COMMAND_STRING = "lb";
    private static final String ACTION_OPTION = "action";
    private static final String ID_OPTION = "id";

    private final LorebookCreateHandler createHandler;
    private final LorebookDeleteHandler deleteHandler;
    private final LorebookEditHandler editHandler;
    private final LorebookGetHandler getHandler;
    private final LorebookListHandler listHandler;

    @Override
    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("handling {} command", COMMAND_STRING);
        LorebookAction action = Optional.ofNullable(event.getOption(ACTION_OPTION))
                .map(OptionMapping::getAsString)
                .flatMap(LorebookAction::byName)
                .orElseThrow(() -> new RuntimeException(USER_ACTION_NOT_FOUND));
        switch (action) {
            case GET -> getHandler.handleCommand(event);
            case CREATE -> createHandler.handleCommand(event);
            case DELETE -> deleteHandler.handleCommand(event);
            case EDIT -> editHandler.handleCommand(event);
            case LIST -> listHandler.handleCommand(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public void handleModal(ModalInteractionEvent event) {

        LOGGER.debug("handling " + COMMAND_STRING + " modal");
        final String modalId = event.getModalId();
        final String actionId = modalId.split("-")[1];
        LOGGER.debug(MessageFormat.format("actionName: {0}", actionId));
        LorebookAction action = Optional.ofNullable(actionId)
                .flatMap(LorebookAction::byName)
                .orElseThrow(() -> new RuntimeException(USER_ACTION_NOT_FOUND));
        switch (action) {
            case CREATE -> createHandler.handleModal(event);
            case DELETE -> deleteHandler.handleModal(event);
            case EDIT -> editHandler.handleModal(event);
            default -> throw new RuntimeException(USER_ACTION_NOT_FOUND);
        }
    }

    @Override
    public SlashCommandData buildCommand() {

        LOGGER.debug("Registering slash command for lorebook operations");
        String actionDescription = MessageFormat.format("One of the following: {0}.", LorebookAction.listAsString());

        return Commands.slash(COMMAND_STRING,
                "Used with subcommands for management of lorebook entries belonging to the current channel's world.")
                .addOption(OptionType.STRING, ACTION_OPTION, actionDescription, true)
                .addOption(OptionType.STRING, ID_OPTION,
                        "ID of the entry to be managed. Usable for delete, edit and get.", false);
    }

    @Override
    public String getName() {

        return COMMAND_STRING;
    }
}
