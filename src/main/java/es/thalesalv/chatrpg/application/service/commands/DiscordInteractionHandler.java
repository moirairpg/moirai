package es.thalesalv.chatrpg.application.service.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface DiscordInteractionHandler {

    default void handleCommand(final SlashCommandInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }

    default void handleModal(final ModalInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }

    default void handleButton(final ButtonInteractionEvent event) {

        throw new UnsupportedOperationException("Event not implemented.");
    }

    SlashCommandData buildCommand();

    String getName();
}
