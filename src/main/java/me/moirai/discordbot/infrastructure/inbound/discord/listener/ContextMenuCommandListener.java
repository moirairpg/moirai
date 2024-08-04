package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
public class ContextMenuCommandListener extends ListenerAdapter {

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {

        String commandName = event.getName();
        Member author = event.getMember();
        Member bot = event.getGuild().retrieveMember(event.getJDA().getSelfUser()).complete();
        Message message = event.getTarget();

        if (!author.getUser().isBot()) {
            switch (commandName) {
                case "(MoirAI) Edit message" -> {
                    String botNickname = StringUtils.isNotBlank(bot.getNickname()) ? bot.getNickname()
                            : bot.getUser().getName();

                    if (!message.getAuthor().getId().equals(bot.getId())) {
                        sendNotification(event, "It's only possible to edit messages sent by " + botNickname);
                        return;
                    }

                    TextInput messageId = TextInput.create("messageId", "Message ID", TextInputStyle.SHORT)
                            .setPlaceholder("Message ID")
                            .setMinLength(1)
                            .setMaxLength(50)
                            .setValue(message.getId())
                            .build();

                    TextInput content = TextInput.create("content", "Content", TextInputStyle.PARAGRAPH)
                            .setPlaceholder("Message content goes here")
                            .setMinLength(1)
                            .setMaxLength(2000)
                            .setValue(message.getContentRaw())
                            .build();

                    Modal modal = Modal.create("editMessage", "Edit message")
                            .addComponents(ActionRow.of(messageId), ActionRow.of(content))
                            .build();

                    event.replyModal(modal).complete();
                }
            }
        }
    }

    private InteractionHook sendNotification(MessageContextInteractionEvent event, String message) {
        return event.reply(message).setEphemeral(true).complete();
    }
}
