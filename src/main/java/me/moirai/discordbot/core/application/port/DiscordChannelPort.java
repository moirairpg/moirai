package me.moirai.discordbot.core.application.port;

import java.util.List;
import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.discord.DiscordMessageData;

public interface DiscordChannelPort {

    DiscordMessageData sendMessageTo(String channelId, String messageContent);

    void sendTemporaryMessageTo(String channelId, String messageContent, int deleteAfterSeconds);

    Optional<DiscordMessageData> getMessageById(String channelId, String messageId);

    void deleteMessageById(String channelId, String messageId);

    DiscordMessageData editMessageById(String channelId, String messageId, String messageContent);

    List<DiscordMessageData> retrieveEntireHistoryFrom(String channelId);

    List<DiscordMessageData> retrieveEntireHistoryBefore(String messageId, String channelId);

    Optional<DiscordMessageData> getLastMessageIn(String channelId);
}
