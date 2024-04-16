package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.MessageEditRequest;
import es.thalesalv.chatrpg.core.application.port.DiscordChannelOperationsPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Lazy })
public class DiscordChannelOperationsAdapter implements DiscordChannelOperationsPort {

    private final GatewayDiscordClient discordClient;

    @Override
    public Mono<Void> sendMessage(String channelId, String messageContent) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel().createMessage(messageContent))
                .then();
    }

    @Override
    public Mono<MessageData> getMessageById(String channelId, String messageId) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel()
                        .getRestMessage(Snowflake.of(messageId))
                        .getData());
    }

    @Override
    public Mono<Void> deleteMessageById(String channelId, String messageId) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel()
                        .getRestMessage(Snowflake.of(messageId))
                        .delete(null));
    }

    @Override
    public Mono<Void> editMessageById(String channelId, String messageId, String messageContent) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel()
                        .getRestMessage(Snowflake.of(messageId))
                        .edit(MessageEditRequest.builder()
                                .contentOrNull(messageContent)
                                .build()))
                .then();
    }

    @Override
    public Mono<List<MessageData>> retrieveLastMessagesFrom(String channelId,
            String startingMessageId, int numberOfMessages) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .map(channel -> channel.getRestChannel()
                        .getMessagesBefore(Snowflake.of(startingMessageId))
                        .toStream()
                        .limit(numberOfMessages)
                        .collect(Collectors.toCollection(ArrayList::new)));
    }

    @Override
    public Mono<List<MessageData>> retrieveLastMessagesInclusiveFrom(String channelId,
            String startingMessageId, int numberOfMessages) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .zipWith(discordClient.getChannelById(Snowflake.of(channelId))
                        .flatMap(channel -> channel.getRestChannel()
                                .getRestMessage(Snowflake.of(startingMessageId))
                                .getData()))
                .map(lastMessageAndChannel -> {
                    Channel channel = lastMessageAndChannel.getT1();
                    MessageData lastMessageData = lastMessageAndChannel.getT2();

                    List<MessageData> messages = channel.getRestChannel()
                            .getMessagesBefore(Snowflake.of(startingMessageId))
                            .toStream()
                            .limit(numberOfMessages)
                            .collect(Collectors.toCollection(ArrayList::new));

                    messages.addFirst(lastMessageData);

                    return messages;
                });
    }
}
