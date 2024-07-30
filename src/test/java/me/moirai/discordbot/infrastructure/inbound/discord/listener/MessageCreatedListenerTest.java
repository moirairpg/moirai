// package me.moirai.discordbot.infrastructure.inbound.discord.listener;

// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;

// import java.util.Optional;

// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.junit.jupiter.MockitoExtension;

// import discord4j.common.util.Snowflake;
// import discord4j.core.GatewayDiscordClient;
// import discord4j.core.event.domain.message.MessageCreateEvent;
// import discord4j.core.object.entity.Guild;
// import discord4j.core.object.entity.Member;
// import discord4j.core.object.entity.Message;
// import me.moirai.discordbot.common.usecases.UseCaseRunner;
// import reactor.core.publisher.Mono;
// import reactor.test.StepVerifier;

// @ExtendWith(MockitoExtension.class)
// class MessageCreatedListenerTest {

//     @Mock
//     private GatewayDiscordClient gatewayDiscordClient;

//     @Mock
//     private UseCaseRunner useCaseRunner;

//     @Mock
//     private MessageCreateEvent event;

//     @Mock
//     private Message message;

//     @Mock
//     private Guild guild;

//     @Mock
//     private Member author;

//     @Mock
//     private Member bot;

//     @InjectMocks
//     private MessageCreatedListener listener;

//     @Test
//     void testMessageListener_whenMessageIsNotBlankAndNotFromBot_thenUseCaseShouldBeRun() {

//         // Given
//         String messageContent = "Hello, World!";

//         when(message.getId()).thenReturn(Snowflake.of("456"));
//         when(message.getContent()).thenReturn(messageContent);
//         when(message.getChannelId()).thenReturn(Snowflake.of("1234567890"));
//         when(message.getAuthorAsMember()).thenReturn(Mono.just(author));

//         when(guild.getId()).thenReturn(Snowflake.of("789"));

//         when(author.getId()).thenReturn(Snowflake.of("789"));
//         when(author.isBot()).thenReturn(false);

//         when(bot.getId()).thenReturn(Snowflake.of("1234"));
//         when(bot.getUsername()).thenReturn("BotUser");

//         when(event.getClient()).thenReturn(gatewayDiscordClient);
//         when(event.getMessage()).thenReturn(message);
//         when(event.getGuild()).thenReturn(Mono.just(guild));
//         when(event.getGuildId()).thenReturn(Optional.of(Snowflake.of("12345")));

//         when(gatewayDiscordClient.getSelfMember(any())).thenReturn(Mono.just(bot));

//         when(useCaseRunner.run(any())).thenReturn(Mono.empty());

//         // When
//         Mono<Void> result = listener.onEvent(event);

//         // Then
//         StepVerifier.create(result).verifyComplete();
//     }

//     @Test
//     void testMessageListener_whenMessageContentIsBlank_thenUseCaseShouldNotBeRun() {

//         // Given
//         String messageContent = "";
//         when(event.getMessage()).thenReturn(message);
//         when(event.getGuildId()).thenReturn(Optional.of(Snowflake.of("12345")));

//         when(message.getContent()).thenReturn(messageContent);
//         when(message.getChannelId()).thenReturn(Snowflake.of("1234567890"));

//         // When
//         Mono<Void> result = listener.onEvent(event);

//         // Then
//         StepVerifier.create(result)
//                 .verifyComplete();
//     }

//     @Test
//     void testMessageListener_whenMessageIsFromBot_thenUseCaseShouldNotBeRun() {

//         // Given
//         String messageContent = "Hello from a bot!";

//         when(event.getClient()).thenReturn(gatewayDiscordClient);
//         when(event.getMessage()).thenReturn(message);
//         when(event.getGuild()).thenReturn(Mono.just(guild));
//         when(event.getGuildId()).thenReturn(Optional.of(Snowflake.of("12345")));

//         when(gatewayDiscordClient.getSelfMember(any())).thenReturn(Mono.just(bot));

//         when(message.getContent()).thenReturn(messageContent);
//         when(message.getChannelId()).thenReturn(Snowflake.of("1234567890"));
//         when(message.getAuthorAsMember()).thenReturn(Mono.just(author));

//         when(author.isBot()).thenReturn(true);

//         // When
//         Mono<Void> result = listener.onEvent(event);

//         // Then
//         StepVerifier.create(result).verifyComplete();
//     }
// }