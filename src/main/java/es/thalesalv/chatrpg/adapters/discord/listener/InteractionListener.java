package es.thalesalv.chatrpg.adapters.discord.listener;

import jakarta.annotation.Nonnull;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.service.commands.lorebook.CommandService;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InteractionListener {

    private final BeanFactory beanFactory;

    private static final String NON_EXISTING_COMMAND = "The command you tried to use does not exist. Please use `create`, `retrieve`, `delete` or `update` as the argument.";
    private static final String LOREBOOK_ENTRY_SERVICE = "LorebookEntryService";
    private static final String DM_ASSIST_SERVICE = "DMAssistService";
    private static final String MISSING_COMMAND_ACTION = "Did not receive slash command action";
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionListener.class);

    public void onSlashCommand(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command event -> {}", event);
            event.deferReply();
            final String eventName = event.getName();
            final String command = Optional.ofNullable(event.getOption("action"))
                    .map(OptionMapping::getAsString).orElseThrow(() -> new IllegalArgumentException(MISSING_COMMAND_ACTION));

            CommandService commandService = null;
            if (eventName.equals("lorebook")) {
                commandService = (CommandService) beanFactory.getBean(command + LOREBOOK_ENTRY_SERVICE);
            } else if (eventName.equals("dmassist")) {
                commandService = (CommandService) beanFactory.getBean(command + DM_ASSIST_SERVICE);
            }

            Optional.ofNullable(commandService)
                    .orElseThrow(() -> new RuntimeException("Command is null"))
                    .handle(event);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.info("User tried a command that does not exist");
            event.reply(NON_EXISTING_COMMAND).setEphemeral(true).complete();
        }
    }

    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

        LOGGER.debug("Received modal interaction event -> {}", event);
        final String modalId = event.getModalId();
        if (modalId.contains("lorebook")) {
            final String command = modalId.split("-")[0];
            final CommandService commandService = (CommandService) beanFactory.getBean(command + LOREBOOK_ENTRY_SERVICE);
            commandService.handle(event);
        }
    }
}
