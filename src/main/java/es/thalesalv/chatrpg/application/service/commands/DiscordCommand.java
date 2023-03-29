package es.thalesalv.chatrpg.application.service.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface DiscordCommand {

    default void handle(final SlashCommandInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }

    default void handle(final ModalInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }

    default void handle(final ButtonInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }
}
