package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.port.inbound.message.DeleteMessage;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class DeleteMessageHandlerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeleteMessageHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIdIsNull() {

        // given
        var command = new DeleteMessage(null, UUID.randomUUID());

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenMessageIdIsNull() {

        // given
        var command = new DeleteMessage(UUID.randomUUID(), null);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldRemoveOnlyThatMessageWhenAMessageIsDeleted() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new DeleteMessage(adventureId, messageId);

        // when
        handler.handle(command);

        // then
        verify(messageRepository).deleteByPublicId(adventureId, messageId);

        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().change()).isEqualTo(TranscriptChange.MESSAGE_REMOVED);
        assertThat(published.getValue().update().messageId()).isEqualTo(messageId);
    }

    @Test
    public void shouldNotMarkNarrationAsPendingWhenAMessageIsDeleted() {

        // given
        var command = new DeleteMessage(UUID.randomUUID(), UUID.randomUUID());

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().isNarrationPending()).isFalse();
    }
}
