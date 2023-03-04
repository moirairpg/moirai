package es.thalesalv.gptbot.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.application.service.commands.CommandService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

    private final BeanFactory beanFactory;

    private static final String LOREBOOK_ENTRY_SERVICE = "LorebookEntryService";
    private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandListener.class);

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command event -> {}", event);
        final String eventName = event.getName();
        if (eventName.equals("lorebook")) {
            event.deferReply();
            final String command = event.getOption("action").getAsString();
            final CommandService commandService = (CommandService) beanFactory.getBean(command + LOREBOOK_ENTRY_SERVICE);
            commandService.handle(event);
        }
    }

    @Override
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
