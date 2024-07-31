package me.moirai.discordbot.infrastructure.outbound.adapter.discord;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.User;
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
    public ChatMessageData sendMessageTo(String channelId, String messageContent) {

        Message messageSent = jda.getTextChannelById(channelId)
                .sendMessage(messageContent)
                .complete();

        Member author = messageSent.getGuild()
                .retrieveMemberById(messageSent.getAuthor().getId())
                .complete();

        return ChatMessageData.builder()
                .authorId(author.getId())
                .authorNickname(author.getNickname())
                .authorUsername(author.getUser().getName())
                .channelId(channelId)
                .content(messageSent.getContentRaw())
                .id(messageSent.getId())
                .build();
    }

    @Override
    public void sendTemporaryMessageTo(String channelId, String messageContent, int deleteAfterSeconds) {

        // FIXME message is not deleted after the specified time; takes longer than
        // expected
        TextChannel channel = jda.getTextChannelById(channelId);
        Message messageSent = channel
                .sendMessage(messageContent + String.format(TEMPORARY_MESSAGE_WARNING, deleteAfterSeconds))
                .complete();

        channel.deleteMessageById(messageSent.getId())
                .completeAfter(deleteAfterSeconds, TimeUnit.SECONDS);
    }

    @Override
    public Optional<ChatMessageData> getMessageById(String channelId, String messageId) {
        try {
            Message message = jda.getTextChannelById(channelId)
                    .retrieveMessageById(messageId)
                    .complete();

            Member author = message.getGuild()
                    .retrieveMemberById(message.getAuthor().getId())
                    .complete();

            return Optional.of(ChatMessageData.builder()
                    .authorId(author.getId())
                    .authorNickname(author.getNickname())
                    .authorUsername(author.getUser().getName())
                    .channelId(channelId)
                    .content(message.getContentRaw())
                    .id(message.getId())
                    .mentionedUsersIds(message.getMentions()
                            .getUsers().stream()
                            .map(User::getId)
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
    public ChatMessageData editMessageById(String channelId, String messageId, String messageContent) {

        Message msgToEdit = jda.getTextChannelById(channelId)
                .retrieveMessageById(messageId)
                .complete()
                .editMessage(messageContent)
                .complete();

        Member author = msgToEdit.getGuild()
                .retrieveMemberById(msgToEdit.getAuthor().getId())
                .complete();

        return ChatMessageData.builder()
                .authorId(author.getId())
                .authorNickname(author.getNickname())
                .authorUsername(author.getUser().getName())
                .channelId(channelId)
                .content(messageContent)
                .id(msgToEdit.getId())
                .build();
    }

    /**
     * Returns the last 100 messages in the channel provided
     *
     * @param channelId        Text channel ID to be searched for messages
     * @param mentionedUserIds List of users mentioned in the original message
     * @return List with all messages retrieved with their metadata (author details
     *         and mentions formatted)
     */
    @Override
    public List<ChatMessageData> retrieveEntireHistoryFrom(String channelId, List<String> mentionedUserIds) {

        TextChannel channel = jda.getTextChannelById(channelId);
        List<Member> mentionedUsers = mentionedUserIds.stream()
                .map(userId -> channel.getGuild()
                        .retrieveMemberById(userId)
                        .complete())
                .toList();

        return MessageHistory.getHistoryFromBeginning(channel)
                .limit(MAX_DISCORD_MESSAGES_ALLOWED)
                .complete()
                .getRetrievedHistory()
                .stream()
                .map(message -> buildMessageResult(channelId, mentionedUsers, message))
                .toList();
    }

    @Override
    public List<ChatMessageData> retrieveEntireHistoryFrom(String channelId) {

        return retrieveEntireHistoryFrom(channelId, Collections.emptyList());
    }

    /**
     * Returns the last 100 messages in the channel provided, before the message
     * supplied
     *
     * @param channelId        Text channel ID to be searched for messages
     * @param mentionedUserIds List of users mentioned in the original message
     * @return List with all messages retrieved with their metadata (author details
     *         and mentions formatted)
     */
    @Override
    public List<ChatMessageData> retrieveEntireHistoryBefore(String messageId, String channelId,
            List<String> mentionedUserIds) {

        TextChannel channel = jda.getTextChannelById(channelId);
        List<Member> mentionedUsers = mentionedUserIds.stream()
                .map(userId -> channel.getGuild()
                        .retrieveMemberById(userId)
                        .complete())
                .toList();

        return MessageHistory.getHistoryBefore(channel, messageId)
                .limit(MAX_DISCORD_MESSAGES_ALLOWED)
                .complete()
                .getRetrievedHistory()
                .stream()
                .map(message -> buildMessageResult(channelId, mentionedUsers, message))
                .toList();
    }

    @Override
    public Optional<ChatMessageData> getLastMessageIn(String channelId) {

        List<ChatMessageData> messageHistrory = retrieveEntireHistoryFrom(channelId, Collections.emptyList());

        return Optional.of(messageHistrory.getFirst());
    }

    private ChatMessageData buildMessageResult(String channelId, List<Member> mentionedUsers, Message message) {

        Member author = message.getGuild()
                .retrieveMemberById(message.getAuthor().getId())
                .complete();

        String formattedContent = formatMessageWithMentions(mentionedUsers, message, author);

        return ChatMessageData.builder()
                .authorId(author.getId())
                .authorNickname(author.getNickname())
                .authorUsername(author.getUser().getName())
                .channelId(channelId)
                .content(formattedContent)
                .id(message.getId())
                .mentionedUsersIds(message.getMentions()
                        .getUsers().stream()
                        .map(User::getId)
                        .toList())
                .build();
    }

    private String formatMessageWithMentions(List<Member> mentionedUsers, Message message, Member author) {

        String messageContent = message.getContentRaw();
        for (Member user : mentionedUsers) {
            messageContent = formatContent(user, messageContent);
        }

        return DefaultStringProcessors.formatChatMessage(author.getNickname())
                .apply(messageContent);
    }

    private String formatContent(Member user, String formattedContent) {

        return formattedContent.replace(
                String.format(USER_MENTION_PLACEHOLDER, user.getId()), user.getNickname());
    }
}
