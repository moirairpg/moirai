package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeInput;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest.Color;
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

    private static final Logger LOG = LoggerFactory.getLogger(ContextMenuCommandListener.class);

    private static final String COMMA_DELIMITER = ", ";
    private static final String CONTENT_FLAGGED_MESSAGE = "Message content was flagged by moderation. The following topics were blocked: %s";
    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again.";
    private static final String TOKEN_REPLY_MESSAGE = "**Characters:** %s\n**Tokens:** %s\n**Token IDs:** %s (contains %s total tokens).";
    private static final String TOO_MUCH_CONTENT_TO_TOKENIZE = "Could not tokenize content. Too much content. Please use the web UI to tokenize large text";
    private static final int DISCORD_MAX_LENGTH = 2000;
    private static final int EPHEMERAL_MESSAGE_TTL = 10;
    private static final int ERROR_MESSAGE_TTL = 10;

    private final UseCaseRunner useCaseRunner;
    private final DiscordChannelPort discordChannelPort;

    public ContextMenuCommandListener(UseCaseRunner useCaseRunner, DiscordChannelPort discordChannelPort) {
        this.useCaseRunner = useCaseRunner;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public void onMessageContextInteraction(MessageContextInteractionEvent event) {
        try {
            String commandName = event.getName();
            Member author = event.getMember();
            Member bot = event.getGuild().retrieveMember(event.getJDA().getSelfUser()).complete();
            Message message = event.getTarget();

            if (!author.getUser().isBot()) {
                event.getChannel().sendTyping().complete();

                switch (commandName) {
                    case "(MoirAI) Edit message" -> {
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
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
                    case "(MoirAI) Tokenize content" -> {
                        InteractionHook interactionHook = sendNotification(event, "Tokenizing input...");
                        String inputToBeTokenized = message.getContentRaw();

                        TokenizeResult tokenizationResult = useCaseRunner.run(TokenizeInput.build(inputToBeTokenized))
                                .orElseThrow(() -> new IllegalStateException("Error tokenizing input"));

                        String finalResult = mapTokenizationResultToMessage(tokenizationResult);

                        if (finalResult.length() > DISCORD_MAX_LENGTH) {
                            updateNotification(interactionHook, TOO_MUCH_CONTENT_TO_TOKENIZE);
                            return;
                        }

                        updateNotification(interactionHook, finalResult);
                    }
                }
            }
        } catch (Exception e) {
            errorNotification(event, e);
        }
    }

    private String mapTokenizationResultToMessage(TokenizeResult tokenizationResult) {

        return String.format(TOKEN_REPLY_MESSAGE, tokenizationResult.getCharacterCount(),
                tokenizationResult.getTokens(), Arrays.toString(tokenizationResult.getTokenIds()),
                tokenizationResult.getTokenCount());
    }

    private InteractionHook sendNotification(MessageContextInteractionEvent event, String message) {
        return event.reply(message).setEphemeral(true).complete();
    }

    private void updateNotification(InteractionHook interactionHook, String newContent) {

        interactionHook.editOriginal(newContent)
                .queue(msg -> msg.delete().queueAfter(EPHEMERAL_MESSAGE_TTL, SECONDS));
    }

    private void errorNotification(MessageContextInteractionEvent event, Throwable error) {

        LOG.error("An error occured while processing message received from Discord", error);
        String authorNickname = isNotBlank(event.getMember().getNickname()) ? event.getMember().getNickname()
                : event.getMember().getUser().getGlobalName();

        DiscordEmbeddedMessageRequest.Builder embedBuilder = DiscordEmbeddedMessageRequest.builder()
                .authorName(authorNickname)
                .authorIconUrl(event.getMember().getAvatarUrl())
                .embedColor(Color.RED);

        if (error instanceof ModerationException moderationException) {
            String flaggedTopics = String.join(COMMA_DELIMITER, moderationException.getFlaggedTopics());
            String message = String.format(CONTENT_FLAGGED_MESSAGE, flaggedTopics);

            DiscordEmbeddedMessageRequest embed = embedBuilder.messageContent(message)
                    .titleText("Inappropriate content detected")
                    .footerText("MoirAI content moderation")
                    .build();

            discordChannelPort.sendTemporaryEmbeddedMessageTo(event.getChannel().getId(), embed, ERROR_MESSAGE_TTL);
            return;
        }

        else if (error instanceof AssetNotFoundException) {
            DiscordEmbeddedMessageRequest embed = embedBuilder.messageContent(error.getMessage())
                    .titleText("Asset requested was not found")
                    .footerText("MoirAI asset management")
                    .build();

            discordChannelPort.sendTemporaryEmbeddedMessageTo(event.getChannel().getId(), embed, ERROR_MESSAGE_TTL);
            return;
        }

        DiscordEmbeddedMessageRequest embed = embedBuilder.messageContent(SOMETHING_WENT_WRONG)
                .titleText("An error occurred")
                .footerText("MoirAI error handling")
                .build();

        discordChannelPort.sendTemporaryEmbeddedMessageTo(event.getChannel().getId(), embed, ERROR_MESSAGE_TTL);
    }
}
