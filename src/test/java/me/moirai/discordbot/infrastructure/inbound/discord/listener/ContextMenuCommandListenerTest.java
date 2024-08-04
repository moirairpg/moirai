package me.moirai.discordbot.infrastructure.inbound.discord.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;

import me.moirai.discordbot.AbstractDiscordTest;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

public class ContextMenuCommandListenerTest extends AbstractDiscordTest {

    @InjectMocks
    private ContextMenuCommandListener listener;

    @Test
    public void editMessageCommand_whenAuthorIsBot_thenDoNothing() {

        // Given
        String eventName = "(MoirAI) Edit message";

        MessageContextInteractionEvent event = mock(MessageContextInteractionEvent.class);

        when(event.getMember()).thenReturn(member);
        when(event.getName()).thenReturn(eventName);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(event.getTarget()).thenReturn(message);
        when(user.isBot()).thenReturn(true);

        // When
        listener.onMessageContextInteraction(event);

        // Then
        verify(event, times(0)).replyModal(any());
    }

    @Test
    public void editMessageCommand_whenInvalidCommand_thenDoNothing() {

        // Given
        String eventName = "invalidName";

        MessageContextInteractionEvent event = mock(MessageContextInteractionEvent.class);

        when(event.getMember()).thenReturn(member);
        when(event.getName()).thenReturn(eventName);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(event.getTarget()).thenReturn(message);

        // When
        listener.onMessageContextInteraction(event);

        // Then
        verify(event, times(0)).replyModal(any());
    }

    @Test
    public void editMessageCommand_whenBotIsNotAuthor_thenSendNotification() {

        // Given
        String botId = "BOTID";
        String eventName = "(MoirAI) Edit message";
        String messageContent = "It's only possible to edit messages sent by " + NICKNAME;

        ReplyCallbackAction messageReplyAction = mock(ReplyCallbackAction.class);
        InteractionHook interactionHook = mock(InteractionHook.class);
        MessageContextInteractionEvent event = mock(MessageContextInteractionEvent.class);

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        when(event.getMember()).thenReturn(member);
        when(event.getName()).thenReturn(eventName);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(event.getTarget()).thenReturn(message);

        when(member.getId()).thenReturn(botId);
        when(event.reply(contentCaptor.capture())).thenReturn(messageReplyAction);
        when(messageReplyAction.setEphemeral(anyBoolean())).thenReturn(messageReplyAction);
        when(messageReplyAction.complete()).thenReturn(interactionHook);

        // When
        listener.onMessageContextInteraction(event);

        // Then
        String contentSent = contentCaptor.getValue();
        assertThat(contentSent).isNotNull()
                .isNotEmpty()
                .isEqualTo(messageContent);
    }

    @Test
    public void editMessageCommand_whenBotIsNotAuthorAndNicknameIsNull_thenSendNotification() {

        // Given
        String botId = "BOTID";
        String eventName = "(MoirAI) Edit message";
        String messageContent = "It's only possible to edit messages sent by " + USERNAME;

        ReplyCallbackAction messageReplyAction = mock(ReplyCallbackAction.class);
        InteractionHook interactionHook = mock(InteractionHook.class);
        MessageContextInteractionEvent event = mock(MessageContextInteractionEvent.class);

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        when(event.getMember()).thenReturn(member);
        when(event.getName()).thenReturn(eventName);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(event.getTarget()).thenReturn(message);

        when(member.getId()).thenReturn(botId);
        when(event.reply(contentCaptor.capture())).thenReturn(messageReplyAction);
        when(messageReplyAction.setEphemeral(anyBoolean())).thenReturn(messageReplyAction);
        when(messageReplyAction.complete()).thenReturn(interactionHook);
        when(member.getNickname()).thenReturn(null);

        // When
        listener.onMessageContextInteraction(event);

        // Then
        String contentSent = contentCaptor.getValue();
        assertThat(contentSent).isNotNull()
                .isNotEmpty()
                .isEqualTo(messageContent);
    }

    @Test
    public void editMessageCommand_whenCalled_thenShouldEditSelectedMessage() {

        // Given
        String botId = "BOTID";
        String eventName = "(MoirAI) Edit message";
        String messageContent = "Some message";

        ModalCallbackAction modalAction = mock(ModalCallbackAction.class);
        ReplyCallbackAction messageReplyAction = mock(ReplyCallbackAction.class);
        InteractionHook interactionHook = mock(InteractionHook.class);
        MessageContextInteractionEvent event = mock(MessageContextInteractionEvent.class);

        ArgumentCaptor<Modal> modalCaptor = ArgumentCaptor.forClass(Modal.class);

        when(event.getMember()).thenReturn(member);
        when(event.getName()).thenReturn(eventName);
        when(event.getGuild()).thenReturn(guild);
        when(event.getJDA()).thenReturn(jda);
        when(event.getTarget()).thenReturn(message);

        when(user.getId()).thenReturn(botId);
        when(member.getId()).thenReturn(botId);
        when(event.reply(anyString())).thenReturn(messageReplyAction);
        when(messageReplyAction.setEphemeral(anyBoolean())).thenReturn(messageReplyAction);
        when(messageReplyAction.complete()).thenReturn(interactionHook);
        when(event.replyModal(modalCaptor.capture())).thenReturn(modalAction);
        when(message.getContentRaw()).thenReturn(messageContent);

        // When
        listener.onMessageContextInteraction(event);

        // Then
        verify(event, times(1)).replyModal(any());

        Modal createdModal = modalCaptor.getValue();

        assertThat(createdModal).isNotNull();
    }
}
