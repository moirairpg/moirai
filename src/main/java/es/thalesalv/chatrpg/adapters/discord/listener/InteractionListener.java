package es.thalesalv.chatrpg.adapters.discord.listener;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
@RequiredArgsConstructor
public class InteractionListener {

    private final BeanFactory beanFactory;

    private static final String CHCONFIG_COMMAND = "ChConfigCommandService";
    private static final String DM_ASSIST_COMMAND = "DMAssistCommandService";
    private static final String LOREBOOK_ENTRY_COMMAND = "LorebookCommandService";

    private static final String SOMETHING_WENT_WRONG_ERROR = "Something went wrong with the command. Please try again.";
    private static final String NON_EXISTING_COMMAND = "The command requested does not exist. Please try again.";
    private static final String MISSING_COMMAND_ACTION = "Did not receive slash command action";
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractionListener.class);

    public void onSlashCommand(SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command event -> {}", event);
            event.deferReply();
            final String eventName = event.getName();
            final String commandName = Optional.ofNullable(event.getOption("action"))
                    .map(OptionMapping::getAsString).orElseThrow(() -> new IllegalArgumentException(MISSING_COMMAND_ACTION));

            DiscordCommand command = null;
            if (eventName.equals("lorebook")) {
                command = (DiscordCommand) beanFactory.getBean(commandName + LOREBOOK_ENTRY_COMMAND);
            } else if (eventName.equals("dmassist")) {
                command = (DiscordCommand) beanFactory.getBean(commandName + DM_ASSIST_COMMAND);
            } else if (eventName.equals("chconfig")) {
                command = (DiscordCommand) beanFactory.getBean(commandName + CHCONFIG_COMMAND);
            }

            Optional.ofNullable(command)
                    .orElseThrow(() -> new NullPointerException("Command is null"))
                    .handle(event);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.info("User tried a command that does not exist");
            event.reply(NON_EXISTING_COMMAND).setEphemeral(true).queue();
        } catch (NullPointerException e) {
            LOGGER.error("Something went wrong when running the command. Null pointer.", e);
            event.reply(SOMETHING_WENT_WRONG_ERROR).setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("Unknown exception caught while running commands", e);
            event.reply(SOMETHING_WENT_WRONG_ERROR).setEphemeral(true).queue();
        }
    }

    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {

        try {
            LOGGER.debug("Received modal interaction event -> {}", event);
            event.deferReply();
            final String modalId = event.getModalId();
            final String commandName = modalId.split("-")[0];
            DiscordCommand command = null;
            if (modalId.contains("lorebook")) {
                command = (DiscordCommand) beanFactory.getBean(commandName + LOREBOOK_ENTRY_COMMAND);
            }  else if (modalId.contains("dmassist")) {
                command = (DiscordCommand) beanFactory.getBean(commandName + DM_ASSIST_COMMAND);
            }

            Optional.ofNullable(command)
                    .orElseThrow(() -> new NullPointerException("Command is null"))
                    .handle(event);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.info("User tried a command that does not exist");
            event.reply(NON_EXISTING_COMMAND).setEphemeral(true).queue();
        } catch (NullPointerException e) {
            LOGGER.info("Something went wrong when running the command. Null pointer.", e);
            event.reply(SOMETHING_WENT_WRONG_ERROR).setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error("Unknown exception caught while running commands", e);
            event.reply(SOMETHING_WENT_WRONG_ERROR).setEphemeral(true).queue();
        }
    }
}
