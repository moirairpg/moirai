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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import me.moirai.discordbot.AbstractDiscordTest;
import me.moirai.discordbot.common.usecases.UseCaseRunner;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.GenerateOutput;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.RetryGeneration;
import me.moirai.discordbot.core.application.usecase.discord.slashcommands.StartCommand;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
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
    public void whenSlashCommandCalled_whenAuthorIsBot_thenDoNothing() {

        // Given
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(true);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(0)).run(any());
    }

    @Test
    public void whenModalCalled_whenInvalidModal_thenDoNothing() {

        // Given
        String command = "invalidCommand";

        when(event.getFullCommandName()).thenReturn(command);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(0)).run(any());
    }

    @Test
    public void retryCommand_whenSuccessfulExecution_thenShouldUpdateNotification() {

        // Given
        String nickname = "nick.name";
        String username = "user.name";
        String command = "retry";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(RetryGeneration.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

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
        String nickname = "nick.name";
        String username = "user.name";
        String command = "go";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(GenerateOutput.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(GenerateOutput.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void startCommand_whenSuccessfulExecution_thenShouldUpdateNotification() {

        // Given
        String nickname = "nick.name";
        String username = "user.name";
        String command = "start";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(StartCommand.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(StartCommand.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void retryCommand_whenSuccessfulExecutionAndNickNameIsNull_thenShouldUpdateNotification() {

        // Given
        String nickname = null;
        String username = "user.name";
        String command = "retry";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(RetryGeneration.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(RetryGeneration.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void goCommand_whenSuccessfulExecutionAndNickNameIsNull_thenShouldUpdateNotification() {

        // Given
        String nickname = null;
        String username = "user.name";
        String command = "go";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(GenerateOutput.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(GenerateOutput.class));

        StepVerifier.create(commandResult)
                .assertNext(message -> assertThat(message).isNotNull())
                .verifyComplete();
    }

    @Test
    public void startCommand_whenSuccessfulExecutionAndNickNameIsNull_thenShouldUpdateNotification() {

        // Given
        String nickname = null;
        String username = "user.name";
        String command = "start";
        Mono<Void> commandResult = Mono.just(mock(Void.class));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(StartCommand.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(user.getName()).thenReturn(username);
        when(member.getNickname()).thenReturn(nickname);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(StartCommand.class));

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
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);

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
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(GenerateOutput.class));

        StepVerifier.create(commandResult)
                .verifyErrorMessage("Error");
    }

    @Test
    public void startCommand_whenErrorThrown_thenShouldUpdateNotificationWithError() {

        // Given
        String command = "start";
        Mono<Void> commandResult = Mono.error(new IllegalStateException("Error"));

        when(event.getFullCommandName()).thenReturn(command);
        when(useCaseRunner.run(any(StartCommand.class))).thenReturn(commandResult);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any(StartCommand.class));

        StepVerifier.create(commandResult)
                .verifyErrorMessage("Error");
    }

    @Test
    public void sayCommand_whenInputIsValid_thenOpenModal() {

        // Given
        String command = "say";
        String modalId = "sayAsBot";

        ModalCallbackAction replyModalAction = mock(ModalCallbackAction.class);

        ArgumentCaptor<Modal> modalCaptor = ArgumentCaptor.forClass(Modal.class);

        when(event.getFullCommandName()).thenReturn(command);
        when(event.getChannel()).thenReturn(channelUnion);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(channelUnion.asTextChannel()).thenReturn(textChannel);
        when(event.replyModal(modalCaptor.capture())).thenReturn(replyModalAction);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        Modal createdModal = modalCaptor.getValue();

        assertThat(createdModal).isNotNull();
        assertThat(createdModal.getId()).isEqualTo(modalId);
    }

    @Test
    public void tokenizeCommand_whenCalled_thenShouldCallUseCase() {

        // Given
        String command = "tokenize";
        String textToTokenize = "This is some text.";
        String result = "This is the result";

        OptionMapping commandParameterContent = mock(OptionMapping.class);
        InteractionHook interactionHook = mock(InteractionHook.class);
        ReplyCallbackAction eventReplyAction = mock(ReplyCallbackAction.class);
        CacheRestAction<Member> memberRetrievalAction = mock(CacheRestAction.class);
        MessageChannelUnion baseChannel = mock(MessageChannelUnion.class);
        WebhookMessageEditAction<Message> editAction = mock(WebhookMessageEditAction.class);

        when(event.getFullCommandName()).thenReturn(command);
        when(event.getChannel()).thenReturn(baseChannel);
        when(baseChannel.asTextChannel()).thenReturn(textChannel);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(jda.getSelfUser()).thenReturn(selfUser);
        when(guild.retrieveMember(any(SelfUser.class))).thenReturn(memberRetrievalAction);
        when(memberRetrievalAction.complete()).thenReturn(member);
        when(event.getMember()).thenReturn(member);
        when(member.getUser()).thenReturn(user);
        when(user.isBot()).thenReturn(false);
        when(event.reply(anyString())).thenReturn(eventReplyAction);
        when(eventReplyAction.setEphemeral(anyBoolean())).thenReturn(eventReplyAction);
        when(eventReplyAction.complete()).thenReturn(interactionHook);
        when(interactionHook.editOriginal(anyString())).thenReturn(editAction);
        when(editAction.complete()).thenReturn(message);
        when(event.getOption(anyString())).thenReturn(commandParameterContent);
        when(commandParameterContent.getAsString()).thenReturn(textToTokenize);
        when(useCaseRunner.run(any())).thenReturn(result);

        // When
        listener.onSlashCommandInteraction(event);

        // Then
        verify(useCaseRunner, times(1)).run(any());
    }
}
