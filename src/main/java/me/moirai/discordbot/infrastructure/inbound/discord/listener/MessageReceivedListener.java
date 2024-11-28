package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.exception.ModerationException;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.helper.ChannelConfigHelper;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.AuthorModeRequest;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.ChatModeRequest;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.RpgModeRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.DiscordEmbeddedMessageRequest.Color;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Component
public class MessageReceivedListener extends ListenerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(SlashCommandListener.class);

    private static final String COMMA_DELIMITER = ", ";
    private static final String CONTENT_FLAGGED_MESSAGE = "Message content was flagged by moderation. The following topics were blocked: %s";
    private static final String SOMETHING_WENT_WRONG = "Something went wrong. Please try again.";
    private static final int ERROR_MESSAGE_TTL = 10;

    private final UseCaseRunner useCaseRunner;
    private final ChannelConfigHelper channelConfigHelper;
    private final DiscordChannelPort discordChannelPort;

    public MessageReceivedListener(UseCaseRunner useCaseRunner,
            ChannelConfigHelper channelConfigHelper,
            DiscordChannelPort discordChannelPort) {

        this.useCaseRunner = useCaseRunner;
        this.channelConfigHelper = channelConfigHelper;
        this.discordChannelPort = discordChannelPort;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        try {
            Message message = event.getMessage();
            Member author = event.getMember();
            Member bot = event.getGuild().getMember(event.getJDA().getSelfUser());
            List<String> mentions = message.getMentions()
                    .getMembers()
                    .stream()
                    .map(Member::getId)
                    .toList();

            String guildId = event.getGuild().getId();
            String channelId = event.getChannel().getId();
            String messageContent = message.getContentRaw();
            String gameMode = channelConfigHelper.getGameModeByDiscordChannelId(channelId);

            if (StringUtils.isNoneBlank(messageContent, gameMode) && !author.getUser().isBot()) {
                String botUsername = bot.getUser().getName();
                String botNickname = isNotBlank(bot.getNickname()) ? bot.getNickname() : botUsername;

                switch (gameMode) {
                    case "CHAT" -> {
                        ChatModeRequest request = ChatModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                    case "RPG" -> {
                        RpgModeRequest request = RpgModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                    case "AUTHOR" -> {
                        AuthorModeRequest request = AuthorModeRequest.builder()
                                .authordDiscordId(author.getId())
                                .channelId(channelId)
                                .messageId(message.getId())
                                .guildId(guildId)
                                .isBotMentioned(mentions.contains(bot.getId()))
                                .mentionedUsersIds(mentions)
                                .botUsername(botUsername)
                                .botNickname(botNickname)
                                .build();

                        useCaseRunner.run(request)
                                .doOnError(error -> errorNotification(event, error))
                                .subscribe();
                    }
                }
            }
        } catch (Exception e) {
            errorNotification(event, e);
        }
    }

    private void errorNotification(MessageReceivedEvent event, Throwable error) {

        LOG.error("An error occured while processing message received from Discord", error);
        String authorNickname = isNotBlank(event.getMember().getNickname()) ? event.getMember().getNickname()
                : event.getMember().getUser().getGlobalName();

        DiscordEmbeddedMessageRequest.Builder embedBuilder = DiscordEmbeddedMessageRequest.builder()
                .authorName(authorNickname)
                .authorIconUrl(event.getAuthor().getAvatarUrl())
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
