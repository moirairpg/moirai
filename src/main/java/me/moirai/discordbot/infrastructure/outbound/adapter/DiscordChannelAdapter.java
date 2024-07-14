package me.moirai.discordbot.infrastructure.outbound.adapter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.MessageEditRequest;
import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.core.application.port.DiscordChannelPort;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.response.ChatMessageData;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DiscordChannelAdapter implements DiscordChannelPort {

    private static final String USER_MENTION_PLACEHOLDER = "<@%s>";
    private static final int MAX_DISCORD_MESSAGES_ALLOWED = 100;

    private final GatewayDiscordClient discordClient;
    private final DiscordUserDetailsPort discordUserDetailsPort;

    @Lazy
    public DiscordChannelAdapter(GatewayDiscordClient discordClient, DiscordUserDetailsPort discordUserDetailsPort) {
        this.discordClient = discordClient;
        this.discordUserDetailsPort = discordUserDetailsPort;
    }

    @Override
    public Mono<Void> sendMessage(String channelId, String messageContent) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel().createMessage(messageContent))
                .then();
    }

    @Override
    public Mono<Void> sendTemporaryMessage(String channelId, String messageContent, int deleteAfterSeconds) {

        return discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel()
                        .createMessage(messageContent)
                        .flatMap(messageSent -> discordClient.getMessageById(
                                Snowflake.of(channelId), Snowflake.of(messageSent.id()))
                                .delayElement(Duration.ofSeconds(deleteAfterSeconds))
                                .map(message -> message.delete())))
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

        Mono<Channel> channelMono = discordClient.getChannelById(Snowflake.of(channelId));
        Mono<MessageData> messageDataMono = discordClient.getChannelById(Snowflake.of(channelId))
                .flatMap(channel -> channel.getRestChannel().getRestMessage(Snowflake.of(startingMessageId)).getData());

        return Mono.zip(channelMono, messageDataMono)
                .map(zipped -> {
                    Channel channel = zipped.getT1();
                    MessageData lastMessageData = zipped.getT2();

                    List<MessageData> messages = channel.getRestChannel()
                            .getMessagesBefore(Snowflake.of(startingMessageId))
                            .toStream()
                            .limit(numberOfMessages)
                            .collect(Collectors.toCollection(ArrayList::new));

                    messages.addFirst(lastMessageData);

                    return messages;
                });
    }

    @Override
    public Mono<List<MessageData>> retrieveEntireHistoryFrom(String channelId, String startingMessageId) {

        return retrieveLastMessagesFrom(channelId, startingMessageId, MAX_DISCORD_MESSAGES_ALLOWED);
    }

    @Override
    public Mono<List<ChatMessageData>> retrieveEntireHistoryFrom(String guildId, String channelId,
            String startingMessageId, List<String> mentionedUserIds) {

        Mono<Guild> guildMono = discordClient.getGuildById(Snowflake.of(guildId));
        Mono<List<Member>> mentionedUsersMono = guildMono
                .flatMap(guild -> getMentionedUsersAsGuildMembers(mentionedUserIds, guild));

        return Mono
                .zip(retrieveEntireHistoryFrom(channelId, startingMessageId), guildMono, mentionedUsersMono)
                .flatMap(zipped -> {
                    List<MessageData> originalMessages = zipped.getT1();
                    Guild guild = zipped.getT2();
                    List<Member> mentionedUsers = zipped.getT3();

                    return Flux.concat(originalMessages.stream().map(originalMessage -> {
                        return guild.getMemberById(Snowflake.of(originalMessage.author().id()))
                                .map(author -> {
                                    String formattedContent = originalMessage.content();
                                    for (Member user : mentionedUsers) {
                                        formattedContent = formatContent(user, formattedContent);
                                    }

                                    formattedContent = DefaultStringProcessors
                                            .formatChatMessage(author.getDisplayName())
                                            .apply(formattedContent);

                                    return ChatMessageData.builder()
                                            .id(originalMessage.id().asString())
                                            .authorId(author.getId().asString())
                                            .channelId(originalMessage.channelId().asString())
                                            .authorNickname(author.getNickname()
                                                    .orElse(author.getUsername()))
                                            .authorUsername(author.getUsername())
                                            .content(formattedContent)
                                            .build();

                                });
                    }).toList())
                            .collectList();
                });
    }

    private Mono<List<Member>> getMentionedUsersAsGuildMembers(List<String> mentionedUserIds, Guild guild) {

        return Flux.concat(mentionedUserIds.stream()
                .map(discordUserDetailsPort::getUserById)
                .map(usersMono -> usersMono
                        .flatMap(user -> guild.getMemberById(Snowflake.of(user.getId()))))
                .toList())
                .collectList();
    }

    private String formatContent(Member user, String formattedContent) {

        return formattedContent.replace(String.format(USER_MENTION_PLACEHOLDER, user.getId().asString()),
                user.getDisplayName());
    }
}
