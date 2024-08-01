package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.GenerateOutput;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.RetryGeneration;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SuppressWarnings("unchecked")
public class SlashCommandListenerTest extends AbstractDiscordTest {

    private static final String GUILD_ID = "GLDID";
    private static final String CHANNEL_ID = "CHID";
    private static final String USERNAME = "user.name";
    private static final String NICKNAME = "nickname";

    @Mock
    private SlashCommandInteractionEvent event;

    @Mock
    private UseCaseRunner useCaseRunner;

    @InjectMocks
    private SlashCommandListener listener;

    @BeforeEach
    public void beforeEach() {

        InteractionHook interactionHook = mock(InteractionHook.class);
        ReplyCallbackAction eventReplyAction = mock(ReplyCallbackAction.class);
        CacheRestAction<Member> memberRetrievalAction = mock(CacheRestAction.class);
        MessageChannelUnion baseChannel = mock(MessageChannelUnion.class);
        WebhookMessageEditAction<Message> editAction = mock(WebhookMessageEditAction.class);

        when(event.getJDA()).thenReturn(jda);
        when(event.getChannel()).thenReturn(baseChannel);
        when(baseChannel.asTextChannel()).thenReturn(textChannel);
        when(event.getGuild()).thenReturn(guild);
        when(event.getUser()).thenReturn(user);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(guild.retrieveMember(any(User.class))).thenReturn(memberRetrievalAction);
        when(guild.retrieveMember(any(SelfUser.class))).thenReturn(memberRetrievalAction);
        when(memberRetrievalAction.complete()).thenReturn(member);
        when(event.reply(anyString())).thenReturn(eventReplyAction);
        when(eventReplyAction.setEphemeral(anyBoolean())).thenReturn(eventReplyAction);
        when(eventReplyAction.complete()).thenReturn(interactionHook);
        when(user.getName()).thenReturn(USERNAME);
        when(member.getNickname()).thenReturn(NICKNAME);
        when(guild.getId()).thenReturn(GUILD_ID);
        when(textChannel.getId()).thenReturn(CHANNEL_ID);
        when(member.getUser()).thenReturn(user);
        when(interactionHook.editOriginal(anyString())).thenReturn(editAction);
        when(editAction.complete()).thenReturn(message);
    }

    @Test
    public void retryCommand_whenSuccessfulExecution_thenShouldUpdateNotification() {

        // Given
        String command = "retry";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(RetryGeneration.class))).thenReturn(commandResult);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(RetryGeneration.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void goCommand_whenSuccessfulExecution_thenShouldUpdateNotification() {

        // Given
        String command = "go";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(GenerateOutput.class))).thenReturn(commandResult);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(GenerateOutput.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void retryCommand_whenErrorThrown_thenShouldUpdateNotificationWithError() {

        // Given
        String command = "retry";
        Mono<Void> commandResult = Mono.error(new IllegalStateException("Error"));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(RetryGeneration.class))).thenReturn(commandResult);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(RetryGeneration.class));

        StepVerifier.create(commandResult)
                .verifyErrorMessage("Error");
    }

    @Test
    public void goCommand_whenErrorThrown_thenShouldUpdateNotificationWithError() {

        // Given
        String command = "go";
        Mono<Void> commandResult = Mono.error(new IllegalStateException("Error"));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(GenerateOutput.class))).thenReturn(commandResult);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(GenerateOutput.class));

        StepVerifier.create(commandResult)
                .verifyErrorMessage("Error");
    }
}
