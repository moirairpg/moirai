package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.Channel;
import discord4j.discordjson.json.MessageData;
import discord4j.discordjson.json.MessageEditRequest;
import discord4j.discordjson.json.UserData;
import discord4j.rest.entity.RestChannel;
import discord4j.rest.entity.RestMessage;
import es.thalesalv.chatrpg.common.fixture.MessageDataFixture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class DiscordChannelAdapterTest {

    @Mock
    private GatewayDiscordClient discordClient;

    @Mock
    private Channel channel;

    @Mock
    private MessageData messageData;

    @Mock
    private RestMessage restMessage;

    @Mock
    private RestChannel restChannel;

    @InjectMocks
    private DiscordChannelAdapter adapter;

    @Test
    void sendMessage_whenCalled_thenMessageShouldBeSent() {

        // Given
        String channelId = "123";
        String messageContent = "Hello, World!";

        when(discordClient.getChannelById(Snowflake.of(channelId)))
                .thenReturn(Mono.just(channel));

        when(channel.getRestChannel())
                .thenReturn(restChannel);

        when(restChannel.createMessage(messageContent))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = adapter.sendMessage(channelId, messageContent);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void sendTemporaryMessage_whenCalled_thenMessageShouldBeSent() {

        // Given
        int deleteMessageAfterSeconds = 5;
        String channelId = "123";
        String messageContent = "Hello, World!";

        when(discordClient.getChannelById(Snowflake.of(channelId)))
                .thenReturn(Mono.just(channel));

        when(channel.getRestChannel())
                .thenReturn(restChannel);

        when(restChannel.createMessage(messageContent))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = adapter.sendTemporaryMessage(channelId, messageContent, deleteMessageAfterSeconds);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void deleteMessageById_whenCalled_thenMessageShouldBeDeleted() {

        // Given
        String channelId = "123";
        String messageId = "456";

        when(discordClient.getChannelById(Snowflake.of(channelId)))
                .thenReturn(Mono.just(channel));

        when(channel.getRestChannel())
                .thenReturn(restChannel);

        when(restChannel.getRestMessage(any()))
                .thenReturn(restMessage);

        when(restChannel.getRestMessage(Snowflake.of(messageId))
                .delete(null))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = adapter.deleteMessageById(channelId, messageId);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void editMessageById_whenCalled_thenMessageShouldBeEdited() {

        // Given
        String channelId = "123";
        String messageId = "456";
        String newMessageContent = "Edited message";

        when(discordClient.getChannelById(Snowflake.of(channelId)))
                .thenReturn(Mono.just(channel));

        when(channel.getRestChannel())
                .thenReturn(restChannel);

        when(restChannel.getRestMessage(any()))
                .thenReturn(restMessage);

        when(restChannel.getRestMessage(Snowflake.of(messageId))
                .edit(MessageEditRequest.builder().contentOrNull(newMessageContent).build()))
                .thenReturn(Mono.empty());

        // When
        Mono<Void> result = adapter.editMessageById(channelId, messageId, newMessageContent);

        // Then
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    void retrieveLastMessages_whenFiveMessagesWanted_thenHistoryShouldReturnFiveMessages() {

        // Given
        String channelId = "123";
        String messageId = "456";
        int amountOfMessagesToRetrieve = 5;
        int expectedMessagesInEnd = 6;

        Flux<MessageData> messageHistory = getMessageHistory();

        when(discordClient.getChannelById(Snowflake.of(channelId)))
                .thenReturn(Mono.just(channel));

        when(channel.getRestChannel())
                .thenReturn(restChannel);

        when(restChannel.getMessagesBefore(any()))
                .thenReturn(messageHistory);

        when(restChannel.getRestMessage(any()))
                .thenReturn(restMessage);

        when(restMessage.getData())
                .thenReturn(Mono.just(MessageDataFixture.messageData()
                        .content("Last message")
                        .build()));

        // When
        Mono<List<MessageData>> result = adapter.retrieveLastMessagesFrom(channelId, messageId,
                amountOfMessagesToRetrieve);

        // Then
        StepVerifier.create(result)
                .consumeNextWith(messages -> {
                    assertThat(messages).hasSize(expectedMessagesInEnd);
                    assertThat(messages.get(0).content()).isEqualTo("Last message");
                    assertThat(messages.get(1).content()).isEqualTo("Message 1");
                    assertThat(messages.get(2).content()).isEqualTo("Message 2");
                    assertThat(messages.get(3).content()).isEqualTo("Message 3");
                    assertThat(messages.get(4).content()).isEqualTo("Message 4");
                    assertThat(messages.get(5).content()).isEqualTo("Message 5");
                })
                .verifyComplete();
    }

    private Flux<MessageData> getMessageHistory() {

        return Flux.just(
                MessageData.builder()
                        .id(1)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 1")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(2)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 2")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(3)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 3")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(4)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 4")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(5)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 5")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(6)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 6")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(7)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 7")
                        .channelId(432647326472L)
                        .build(),
                MessageData.builder()
                        .id(8)
                        .author(mock(UserData.class))
                        .timestamp("64732647326432")
                        .mentionEveryone(false)
                        .tts(false)
                        .pinned(false)
                        .type(1)
                        .content("Message 8")
                        .channelId(432647326472L)
                        .build());
    }
}
