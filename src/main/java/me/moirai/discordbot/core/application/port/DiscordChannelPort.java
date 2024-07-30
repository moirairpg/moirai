package me.moirai.discordbot.core.application.port;

import java.util.List;
import java.util.Optional;

import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;

public interface DiscordChannelPort {

    ChatMessageData sendMessageTo(String channelId, String messageContent);

    void sendTemporaryMessageTo(String channelId, String messageContent, int deleteAfterSeconds);

    Optional<ChatMessageData> getMessageById(String channelId, String messageId);

    void deleteMessageById(String channelId, String messageId);

    ChatMessageData editMessageById(String channelId, String messageId, String messageContent);

    List<ChatMessageData> retrieveEntireHistoryFrom(String channelId, List<String> mentionedUserIds);

    List<ChatMessageData> retrieveEntireHistoryBefore(String messageId, String channelId,
            List<String> mentionedUserIds);
}
