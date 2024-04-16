package es.thalesalv.chatrpg.core.application.port;

import java.util.List;

import discord4j.discordjson.json.MessageData;
import reactor.core.publisher.Mono;

public interface DiscordChannelOperationsPort {

    Mono<Void> sendMessage(String channelId, String messageContent);

    Mono<MessageData> getMessageById(String channelId, String messageId);

    Mono<Void> deleteMessageById(String channelId, String messageId);

    Mono<Void> editMessageById(String channelId, String messageId, String messageContent);

    Mono<List<MessageData>> retrieveLastMessagesFrom(String channelId, String startingMessageId, int numberOfMessages);

    Mono<List<MessageData>> retrieveLastMessagesInclusiveFrom(String channelId, String startingMessageId,
            int numberOfMessages);
}
