package es.thalesalv.chatrpg.application.service.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandService {

    void handle(final SlashCommandInteractionEvent event);
    void handle(final ModalInteractionEvent event);
}
