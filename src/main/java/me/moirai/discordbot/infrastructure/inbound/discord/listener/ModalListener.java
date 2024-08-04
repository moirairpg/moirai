package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.contextmenu.EditMessage;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.SayCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;

@Component
public class ModalListener extends ListenerAdapter {

    private static final String MESSAGE_EDITED = "Message edited.";
    private static final String WAITING_FOR_INPUT = "Waiting for input...";
    private static final String MESSAGE_ID = "messageId";
    private static final String MESSAGE_CONTENT = "content";
    private static final String INPUT_SENT = "Input sent.";

    private final UseCaseRunner useCaseRunner;

    public ModalListener(UseCaseRunner useCaseRunner) {
        this.useCaseRunner = useCaseRunner;
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {

        String modalId = event.getModalId();
        Guild guild = event.getGuild();
        TextChannel textChannel = event.getChannel().asTextChannel();
        User author = event.getMember().getUser();
        Member bot = guild.retrieveMember(event.getJDA().getSelfUser()).complete();

        if (!author.isBot()) {
            switch (modalId) {
                case "sayAsBot" -> {
                    InteractionHook interactionHook = sendNotification(event, WAITING_FOR_INPUT);
                    String messageContent = event.getValue(MESSAGE_CONTENT).getAsString();

                    SayCommand useCase = SayCommand.build(textChannel.getId(), messageContent);

                    useCaseRunner.run(useCase);

                    updateNotification(interactionHook, INPUT_SENT);
                }
                case "editMessage" -> {
                    InteractionHook interactionHook = sendNotification(event, WAITING_FOR_INPUT);
                    String messageContent = event.getValue(MESSAGE_CONTENT).getAsString();
                    String messageId = event.getValue(MESSAGE_ID).getAsString();
                    Message message = textChannel.retrieveMessageById(messageId).complete();

                    if (!message.getAuthor().getId().equals(bot.getId())) {
                        updateNotification(interactionHook,
                                "It's only possible to edit messages sent by " + getBotNickname(bot));
                        return;
                    }

                    EditMessage useCase = EditMessage.build(textChannel.getId(), messageId, messageContent);

                    useCaseRunner.run(useCase);

                    updateNotification(interactionHook, MESSAGE_EDITED);
                }
            }
        }
    }

    private String getBotNickname(Member bot) {

        return StringUtils.isNotBlank(bot.getNickname()) ? bot.getNickname()
                : bot.getUser().getName();
    }

    private Message updateNotification(InteractionHook interactionHook, String newContent) {
        return interactionHook.editOriginal(newContent).complete();
    }

    private InteractionHook sendNotification(ModalInteractionEvent event, String message) {
        return event.reply(message).setEphemeral(true).complete();
    }
}
