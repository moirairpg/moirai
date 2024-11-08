package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.helper.ChannelConfigHelper;
import me.moirai.discordbot.core.application.usecase.discord.messagereceived.ChatModeRequest;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MessageReceivedListenerTest extends AbstractDiscordTest {

    @Mock
    private UseCaseRunner useCaseRunner;

    @Mock
    private ChannelConfigHelper channelConfigHelper;

    @InjectMocks
    private MessageReceivedListener listener;

    @Test
    public void messageListener_whenMessageReceived_thenCallUseCase() {

        // Given
        String guildId = "GDID";
        String channelId = "CHID";
        String userId = "USRID";
        String messageId = "MSGID";
        String username = "user.name";
        String nickname = "nickname";
        String messageContent = "content";
        String gameMode = "CHAT";

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Mentions mentions = mock(Mentions.class);

        when(event.getMessage()).thenReturn(message);
        when(event.getMember()).thenReturn(member);
        when(event.getGuild()).thenReturn(guild);
        when(guild.getMember(any(SelfUser.class))).thenReturn(member);
        when(message.getMentions()).thenReturn(mentions);
        when(mentions.getMembers()).thenReturn(Collections.emptyList());
        when(guild.getId()).thenReturn(guildId);
        when(event.getChannel()).thenReturn(channelUnion);
        when(channelUnion.getId()).thenReturn(channelId);
        when(message.getContentRaw()).thenReturn(messageContent);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);
        when(member.getId()).thenReturn(userId);
        when(message.getId()).thenReturn(messageId);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(channelConfigHelper.getGameModeByDiscordChannelId(anyString())).thenReturn(gameMode);

        Mono<Void> useCaseResult = Mono.just(mock(Void.class));

        when(useCaseRunner.run(any(ChatModeRequest.class))).thenReturn(useCaseResult);

        // When
        listener.onMessageReceived(event);

        // Then
        StepVerifier.create(useCaseResult)
                .assertNext(result -> assertThat(result).isNotNull())
                .verifyComplete();
    }

    @Test
    public void messageListener_whenMessageReceivedAndNicknameNull_thenCallUseCase() {

        // Given
        String guildId = "GDID";
        String channelId = "CHID";
        String userId = "USRID";
        String messageId = "MSGID";
        String username = "user.name";
        String nickname = null;
        String messageContent = "content";
        String gameMode = "CHAT";

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Mentions mentions = mock(Mentions.class);

        when(event.getMessage()).thenReturn(message);
        when(event.getMember()).thenReturn(member);
        when(event.getGuild()).thenReturn(guild);
        when(guild.getMember(any(SelfUser.class))).thenReturn(member);
        when(message.getMentions()).thenReturn(mentions);
        when(mentions.getMembers()).thenReturn(Collections.emptyList());
        when(guild.getId()).thenReturn(guildId);
        when(event.getChannel()).thenReturn(channelUnion);
        when(channelUnion.getId()).thenReturn(channelId);
        when(message.getContentRaw()).thenReturn(messageContent);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);
        when(member.getId()).thenReturn(userId);
        when(message.getId()).thenReturn(messageId);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(channelConfigHelper.getGameModeByDiscordChannelId(anyString())).thenReturn(gameMode);

        Mono<Void> useCaseResult = Mono.just(mock(Void.class));

        when(useCaseRunner.run(any(ChatModeRequest.class))).thenReturn(useCaseResult);

        // When
        listener.onMessageReceived(event);

        // Then
        StepVerifier.create(useCaseResult)
                .assertNext(result -> assertThat(result).isNotNull())
                .verifyComplete();
    }

    @Test
    public void messageListener_whenSenderIsBot_thenDoNothing() {

        // Given
        String guildId = "GDID";
        String channelId = "CHID";
        String messageContent = "content";
        String gameMode = "CHAT";

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Mentions mentions = mock(Mentions.class);

        when(event.getMessage()).thenReturn(message);
        when(event.getMember()).thenReturn(member);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(guild.getMember(any(SelfUser.class))).thenReturn(member);
        when(guild.getId()).thenReturn(guildId);
        when(event.getChannel()).thenReturn(channelUnion);
        when(channelUnion.getId()).thenReturn(channelId);
        when(message.getContentRaw()).thenReturn(messageContent);
        when(message.getMentions()).thenReturn(mentions);
        when(mentions.getMembers()).thenReturn(Collections.emptyList());
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(true);
        when(channelConfigHelper.getGameModeByDiscordChannelId(anyString())).thenReturn(gameMode);

        // When
        listener.onMessageReceived(event);

        // Then
        verify(useCaseRunner, times(0)).run(any());
    }

    @Test
    public void messageListener_whenMessageIsEmpty_thenDoNothing() {

        // Given
        String guildId = "GDID";
        String channelId = "CHID";
        String gameMode = "CHAT";

        MessageReceivedEvent event = mock(MessageReceivedEvent.class);
        Mentions mentions = mock(Mentions.class);

        when(event.getMessage()).thenReturn(message);
        when(event.getMember()).thenReturn(member);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(guild.getMember(any(SelfUser.class))).thenReturn(member);
        when(guild.getId()).thenReturn(guildId);
        when(event.getChannel()).thenReturn(channelUnion);
        when(channelUnion.getId()).thenReturn(channelId);
        when(message.getContentRaw()).thenReturn(EMPTY);
        when(message.getMentions()).thenReturn(mentions);
        when(mentions.getMembers()).thenReturn(Collections.emptyList());
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelConfigHelper.getGameModeByDiscordChannelId(anyString())).thenReturn(gameMode);

        // When
        listener.onMessageReceived(event);

        // Then
        verify(useCaseRunner, times(0)).run(any());
    }
}
