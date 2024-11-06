package me.moirai.discordbot.infrastructure.outbound.adapter.discord;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@Component
public class DiscordChannelAdapter implements DiscordChannelPort {

    private static final String USER_MENTION_PLACEHOLDER = "<@%s>";
    private static final int MAX_DISCORD_MESSAGES_ALLOWED = 100;
    private static final String TEMPORARY_MESSAGE_WARNING = "\n\nThis message will disappear after %s seconds.";

    private final JDA jda;

    @Lazy
    public DiscordChannelAdapter(JDA jda) {

        this.jda = jda;
    }

    @Override
    public DiscordMessageData sendMessageTo(String channelId, String messageContent) {

        Message messageSent = jda.getTextChannelById(channelId)
                .sendMessage(messageContent)
                .complete();

        Member author = messageSent.getGuild()
                .retrieveMemberById(messageSent.getAuthor().getId())
                .complete();

        return DiscordMessageData.builder()
                .id(messageSent.getId())
                .channelId(channelId)
                .content(messageSent.getContentRaw())
                .author(DiscordUserDetails.builder()
                        .id(author.getId())
                        .nickname(isNotEmpty(author.getNickname()) ? author.getNickname() : author.getUser().getGlobalName())
                        .username(author.getUser().getName())
                        .mention(author.getAsMention())
                        .build())
                .build();
    }

    @Override
    public void sendTemporaryMessageTo(String channelId, String messageContent, int deleteAfterSeconds) {

        // FIXME message is not deleted after the specified time
        TextChannel channel = jda.getTextChannelById(channelId);
        Message messageSent = channel
                .sendMessage(messageContent + String.format(TEMPORARY_MESSAGE_WARNING, deleteAfterSeconds))
                .complete();

        channel.deleteMessageById(messageSent.getId())
                .completeAfter(deleteAfterSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<DiscordMessageData> getMessageById(String channelId, String messageId) {
        try {
            Message message = jda.getTextChannelById(channelId)
                    .retrieveMessageById(messageId)
                    .complete();

            Member author = message.getGuild()
                    .retrieveMemberById(message.getAuthor().getId())
                    .complete();

            String formattedContent = formatMessageWithMentions(message.getMentions().getMembers(), message, author);

            return Optional.of(DiscordMessageData.builder()
                    .id(message.getId())
                    .channelId(channelId)
                    .content(formattedContent)
                    .author(DiscordUserDetails.builder()
                            .id(author.getId())
                            .nickname(isNotEmpty(author.getNickname()) ? author.getNickname()
                                    : author.getUser().getGlobalName())
                            .username(author.getUser().getName())
                            .mention(author.getAsMention())
                            .build())
                    .mentionedUsers(message.getMentions()
                            .getMembers().stream()
                            .map(member -> DiscordUserDetails.builder()
                                    .id(member.getId())
                                    .mention(member.getAsMention())
                                    .nickname(isNotEmpty(member.getNickname()) ? member.getNickname()
                                            : member.getUser().getGlobalName())
                                    .username(member.getUser().getName())
                                    .build())
                            .toList())
                    .build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteMessageById(String channelId, String messageId) {

        jda.getTextChannelById(channelId)
                .deleteMessageById(messageId)
                .complete();
    }

    @Override
    public DiscordMessageData editMessageById(String channelId, String messageId, String messageContent) {

        Message msgToEdit = jda.getTextChannelById(channelId)
                .retrieveMessageById(messageId)
                .complete()
                .editMessage(messageContent)
                .complete();

        Member author = msgToEdit.getGuild()
                .retrieveMemberById(msgToEdit.getAuthor().getId())
                .complete();

        return DiscordMessageData.builder()
                .id(msgToEdit.getId())
                .channelId(channelId)
                .content(messageContent)
                .author(DiscordUserDetails.builder()
                        .id(author.getId())
                        .nickname(isNotEmpty(author.getNickname()) ? author.getNickname() : author.getUser().getGlobalName())
                        .username(author.getUser().getName())
                        .mention(author.getAsMention())
                        .build())
                .mentionedUsers(msgToEdit.getMentions()
                        .getMembers().stream()
                        .map(member -> DiscordUserDetails.builder()
                                .id(member.getId())
                                .mention(member.getAsMention())
                                .nickname(isNotEmpty(member.getNickname()) ? member.getNickname()
                                        : member.getUser().getGlobalName())
                                .username(member.getUser().getName())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Returns the last 100 messages in the channel provided
     *
     * @param channelId Text channel ID to be searched for messages
     * @return List with all messages retrieved with their metadata (author details
     *         and mentions formatted)
     */
    @Override
    public List<DiscordMessageData> retrieveEntireHistoryFrom(String channelId) {

        TextChannel channel = jda.getTextChannelById(channelId);

        return MessageHistory.getHistoryFromBeginning(channel)
                .limit(MAX_DISCORD_MESSAGES_ALLOWED)
                .complete()
                .getRetrievedHistory()
                .stream()
                .map(message -> buildMessageResult(channelId, message))
                .toList();
    }

    /**
     * Returns the last 100 messages in the channel provided, before the message
     * supplied
     *
     * @param channelId Text channel ID to be searched for messages
     * @return List with all messages retrieved with their metadata (author details
     *         and mentions formatted)
     */
    @Override
    public List<DiscordMessageData> retrieveEntireHistoryBefore(String messageId, String channelId) {

        TextChannel channel = jda.getTextChannelById(channelId);

        return MessageHistory.getHistoryBefore(channel, messageId)
                .limit(MAX_DISCORD_MESSAGES_ALLOWED)
                .complete()
                .getRetrievedHistory()
                .stream()
                .map(message -> buildMessageResult(channelId, message))
                .toList();
    }

    @Override
    public Optional<DiscordMessageData> getLastMessageIn(String channelId) {

        TextChannel channel = jda.getTextChannelById(channelId);
        return Optional.of(getMessageById(channelId, channel.getLatestMessageId())
                .orElseGet(() -> retrieveEntireHistoryBefore(channel.getLatestMessageId(), channelId).getFirst()));

        // return getMessageById(channelId, channel.getLatestMessageId());
    }

    private DiscordMessageData buildMessageResult(String channelId, Message message) {

        Member author = message.getGuild()
                .retrieveMemberById(message.getAuthor().getId())
                .complete();

        List<Member> mentionedUsers = message.getMentions().getMembers();
        String formattedContent = formatMessageWithMentions(mentionedUsers, message, author);

        return DiscordMessageData.builder()
                .id(message.getId())
                .channelId(channelId)
                .content(formattedContent)
                .author(DiscordUserDetails.builder()
                        .id(author.getId())
                        .nickname(isNotEmpty(author.getNickname()) ? author.getNickname() : author.getUser().getGlobalName())
                        .username(author.getUser().getName())
                        .mention(author.getAsMention())
                        .build())
                .mentionedUsers(mentionedUsers.stream()
                        .map(member -> DiscordUserDetails.builder()
                                .id(member.getId())
                                .mention(member.getAsMention())
                                .nickname(isNotEmpty(member.getNickname()) ? member.getNickname()
                                        : member.getUser().getGlobalName())
                                .username(member.getUser().getName())
                                .build())
                        .toList())
                .build();
    }

    private String formatMessageWithMentions(List<Member> mentionedUsers, Message message, Member author) {

        String authorNickname = StringUtils.isNotBlank(author.getNickname()) ? author.getNickname()
                : author.getUser().getName();

        String messageContent = message.getContentRaw();
        for (Member user : mentionedUsers) {
            messageContent = formatContent(user, messageContent);
        }

        return DefaultStringProcessors.formatChatMessage(authorNickname)
                .apply(messageContent);
    }

    private String formatContent(Member user, String formattedContent) {

        return formattedContent.replace(
                String.format(USER_MENTION_PLACEHOLDER, user.getId()), user.getNickname());
    }
}
