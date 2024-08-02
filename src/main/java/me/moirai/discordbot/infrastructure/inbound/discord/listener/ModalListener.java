package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.SayCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

@Component
public class ModalListener extends ListenerAdapter {

    private static final String INPUT_SENT = "Input sent.";

    private final UseCaseRunner useCaseRunner;

    public ModalListener(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        String modalId = event.getModalId();
        TextChannel textChannel = event.getChannel().asTextChannel();
        User author = event.getMember().getUser();

        if (!author.isBot()) {
            switch (modalId) {
                case "sayAsBot" -> {
                    InteractionHook interactionHook = sendNotification(event, "Waiting for input...");
                    String messageContent = event.getValue("content").getAsString();

                    SayCommand useCase = SayCommand.build(textChannel.getId(), messageContent);

                    useCaseRunner.run(useCase);

                    updateNotification(interactionHook, INPUT_SENT);
                }
            }
        }
    }

    private Message updateNotification(InteractionHook interactionHook, String newContent) {
        return interactionHook.editOriginal(newContent).complete();
    }

    private InteractionHook sendNotification(ModalInteractionEvent event, String message) {
        return event.reply(message).setEphemeral(true).complete();
    }
}
