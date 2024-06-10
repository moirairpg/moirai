package es.thalesalv.chatrpg.core.application.port;

import java.util.List;

import discord4j.discordjson.json.MessageData;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Mono;

public interface DiscordChannelPort {

    Mono<Void> sendMessage(String channelId, String messageContent);

    Mono<Void> sendTemporaryMessage(String channelId, String messageContent, int deleteAfterSeconds);

    Mono<MessageData> getMessageById(String channelId, String messageId);

    Mono<Void> deleteMessageById(String channelId, String messageId);

    Mono<Void> editMessageById(String channelId, String messageId, String messageContent);

    Mono<List<MessageData>> retrieveLastMessagesFrom(String channelId,
            String startingMessageId, int numberOfMessages);

    Mono<List<MessageData>> retrieveEntireHistoryFrom(String channelId, String startingMessageId);

    Mono<List<ChatMessageData>> retrieveEntireHistoryFrom(String guildId, String channelId,
            String startingMessageId, List<String> mentionedUserIds);
}
