package es.thalesalv.gptbot.application.service.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandService {

    void handle(SlashCommandInteractionEvent event);
    void handle(ModalInteractionEvent event);
}
