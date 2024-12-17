package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.adventure.request.GetAdventureByChannelId;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.GoCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.RetryCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.StartCommand;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeInput;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.TokenizeResult;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest.Color;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

@Component
public class SlashCommandListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SlashCommandListener.class);

    private static final String CONTENT = "Content";
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
    private final List<String> goCommandPhrasesBeforeRunning;
    private final List<String> goCommandPhrasesAfterRunning;
    private final List<String> retryCommandPhrasesBeforeRunning;
    private final List<String> retryCommandPhrasesAfterRunning;
    private final List<String> startCommandPhrasesBeforeRunning;
    private final List<String> startCommandPhrasesAfterRunning;

    public SlashCommandListener(
            UseCaseRunner useCaseRunner,
            DiscordChannelPort discordChannelPort,
            @Value("${moirai.discord.bot.commands.go.before-running}") List<String> goCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.go.after-running}") List<String> goCommandPhrasesAfterRunning,
            @Value("${moirai.discord.bot.commands.retry.before-running}") List<String> retryCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.retry.after-running}") List<String> retryCommandPhrasesAfterRunning,
            @Value("${moirai.discord.bot.commands.start.before-running}") List<String> startCommandPhrasesBeforeRunning,
            @Value("${moirai.discord.bot.commands.start.after-running}") List<String> startCommandPhrasesAfterRunning) {

        this.useCaseRunner = useCaseRunner;
        this.discordChannelPort = discordChannelPort;
        this.goCommandPhrasesBeforeRunning = goCommandPhrasesBeforeRunning;
        this.goCommandPhrasesAfterRunning = goCommandPhrasesAfterRunning;
        this.retryCommandPhrasesBeforeRunning = retryCommandPhrasesBeforeRunning;
        this.retryCommandPhrasesAfterRunning = retryCommandPhrasesAfterRunning;
        this.startCommandPhrasesBeforeRunning = startCommandPhrasesBeforeRunning;
        this.startCommandPhrasesAfterRunning = startCommandPhrasesAfterRunning;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        try {
            String command = event.getFullCommandName();
            TextChannel textChannel = event.getChannel().asTextChannel();
            Guild guild = event.getGuild();
            Member bot = guild.retrieveMember(event.getJDA().getSelfUser()).complete();
            User author = event.getMember().getUser();

            if (!author.isBot()) {
                event.getChannel().sendTyping().complete();

                switch (command) {
                    case "retry" -> {
                        InteractionHook interactionHook = sendNotification(event,
                                getCommandPhrase(retryCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        RetryCommand useCase = RetryCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> updateNotification(interactionHook,
                                        getCommandPhrase(retryCommandPhrasesAfterRunning)))
                                .doOnError(error -> errorNotification(interactionHook, error))
                                .subscribe();
                    }
                    case "go" -> {
                        InteractionHook interactionHook = sendNotification(event,
                                getCommandPhrase(goCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        GoCommand useCase = GoCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> updateNotification(interactionHook,
                                        getCommandPhrase(goCommandPhrasesAfterRunning)))
                                .doOnError(error -> errorNotification(interactionHook, error))
                                .subscribe();
                    }
                    case "start" -> {
                        InteractionHook interactionHook = sendNotification(event,
                                getCommandPhrase(startCommandPhrasesBeforeRunning));

                        String botUsername = bot.getUser().getName();
                        String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname()
                                : botUsername;

                        StartCommand useCase = StartCommand.builder()
                                .botId(bot.getId())
                                .botNickname(botNickname)
                                .botUsername(botUsername)
                                .guildId(guild.getId())
                                .channelId(textChannel.getId())
                                .build();

                        useCaseRunner.run(useCase)
                                .doOnSuccess(__ -> updateNotification(interactionHook,
                                        getCommandPhrase(startCommandPhrasesAfterRunning)))
                                .doOnError(error -> updateNotification(interactionHook, error.getMessage()))
                                .subscribe();
                    }
                    case "say" -> {
                        TextInput content = TextInput.create("content", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Message to be sent as the bot")
                                .setMinLength(1)
                                .setMaxLength(2000)
                                .build();

                        Modal modal = Modal.create("sayAsBot", "Say as bot")
                                .addComponents(ActionRow.of(content))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "tokenize" -> {
                        InteractionHook interactionHook = sendNotification(event, "Tokenizing input...");
                        String inputToBeTokenized = event.getOption("input").getAsString();

                        TokenizeResult tokenizationResult = useCaseRunner.run(TokenizeInput.build(inputToBeTokenized))
                                .orElseThrow(() -> new IllegalStateException("Error tokenizing input"));

                        String finalResult = mapTokenizationResultToMessage(tokenizationResult);

                        if (finalResult.length() > DISCORD_MAX_LENGTH) {
                            updateNotification(interactionHook, TOO_MUCH_CONTENT_TO_TOKENIZE);
                            return;
                        }

                        updateNotification(interactionHook, finalResult);
                    }
                    case "remember" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput rememberContent = TextInput
                                .create("rememberContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Important piece of information the AI has to remember about")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getRemember())
                                .build();

                        Modal modal = Modal.create("remember", "Context modifier: remember")
                                .addComponents(ActionRow.of(rememberContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "nudge" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput nudgeContent = TextInput.create("nudgeContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("General instructions for the AI to follow")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getNudge())
                                .build();

                        Modal modal = Modal.create("nudge", "Context modifier: nudge")
                                .addComponents(ActionRow.of(nudgeContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "authorsnote" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput authorsNoteContent = TextInput
                                .create("authorsNoteContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Instructions from the author on how the story should be told")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getAuthorsNote())
                                .build();

                        Modal modal = Modal.create("authorsNote", "Context modifier: author's note")
                                .addComponents(ActionRow.of(authorsNoteContent))
                                .build();

                        event.replyModal(modal).complete();
                    }
                    case "bump" -> {
                        GetAdventureByChannelId request = GetAdventureByChannelId.build(textChannel.getId());
                        GetAdventureResult result = useCaseRunner.run(request);

                        TextInput bumpContent = TextInput.create("bumpContent", CONTENT, TextInputStyle.PARAGRAPH)
                                .setPlaceholder("Reminders inserted between messages to keep AI's act on track")
                                .setMinLength(1)
                                .setMaxLength(50)
                                .setValue(result.getBump())
                                .build();

                        TextInput bumpFrequency = TextInput
                                .create("bumpFrequency", "Insert bump every # messages", TextInputStyle.SHORT)
                                .setPlaceholder("3")
                                .setMinLength(1)
                                .setMaxLength(2)
                                .setValue(String.valueOf(result.getBumpFrequency()))
                                .build();

                        Modal modal = Modal.create("bump", "Context modifier: bump")
                                .addComponents(ActionRow.of(bumpContent), ActionRow.of(bumpFrequency))
                                .build();

                        event.replyModal(modal).complete();
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

    private InteractionHook sendNotification(SlashCommandInteractionEvent event, String message) {
        return event.reply(message).setEphemeral(true).complete();
    }

    private void updateNotification(InteractionHook interactionHook, String newContent) {

        interactionHook.editOriginal(newContent)
                .onSuccess(msg -> msg.delete().completeAfter(EPHEMERAL_MESSAGE_TTL, SECONDS))
                .complete();
    }

    private String getCommandPhrase(List<String> phraseList) {

        int randomIndex = new Random().nextInt(phraseList.size());
        return phraseList.get(randomIndex);
    }

    private void errorNotification(InteractionHook interactionHook, Throwable error) {

        if (error instanceof ModerationException moderationException) {
            String flaggedTopics = String.join(COMMA_DELIMITER, moderationException.getFlaggedTopics());
            String message = String.format(CONTENT_FLAGGED_MESSAGE, flaggedTopics);

            updateNotification(interactionHook, message);
        }

        else if (error instanceof AssetNotFoundException assetNotFoundException) {
            updateNotification(interactionHook, assetNotFoundException.getMessage());
        }

        updateNotification(interactionHook, SOMETHING_WENT_WRONG);
    }

    private void errorNotification(SlashCommandInteractionEvent event, Throwable error) {

        LOG.error("An error occured while processing message received from Discord", error);
        String authorNickname = isNotBlank(event.getMember().getNickname()) ? event.getMember().getNickname()
                : event.getMember().getUser().getGlobalName();

        DiscordEmbeddedMessageRequest.Builder embedBuilder = DiscordEmbeddedMessageRequest.builder()
                .authorName(authorNickname)
                .authorIconUrl(event.getMember().getAvatarUrl())
                .embedColor(Color.RED);

        if (error instanceof ModerationException) {
            ModerationException moderationException = (ModerationException) error;
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
