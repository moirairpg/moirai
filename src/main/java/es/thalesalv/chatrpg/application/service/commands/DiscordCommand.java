package es.thalesalv.chatrpg.application.service.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class DiscordCommand {

    public void handle(final SlashCommandInteractionEvent event) {
        throw new UnsupportedOperationException("Event not implemented.");
    }

    public void handle(final ModalInteractionEvent event) {
        throw new UnsupportedOperationException("Event not implemented.");
    }

    public void handle(final ButtonInteractionEvent event) {
        throw new UnsupportedOperationException("Event not implemented.");
    }
}
