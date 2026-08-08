package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.application.event.message.NarrationRetriedFromMessageEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.port.inbound.message.RetryFromMessage;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class RetryFromMessageHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RetryFromMessageHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // given
        var command = new RetryFromMessage(UUID.randomUUID(), UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenMessageIdIsNull() {

        // given
        var command = new RetryFromMessage(UUID.randomUUID(), null);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldRemoveTheMessageAndEverythingAfterItWhenRetryingFromAMessage() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new RetryFromMessage(adventureId, messageId);

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));

        // when
        handler.handle(command);

        // then
        verify(messageRepository).deleteNewerThanByPublicId(adventureId, messageId);
        verify(messageRepository).deleteByPublicId(adventureId, messageId);

        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getAllValues().get(0);

        assertThat(transcriptChange.update().change()).isEqualTo(TranscriptChange.MESSAGES_REMOVED_FROM);
        assertThat(transcriptChange.update().messageId()).isEqualTo(messageId);
        assertThat(transcriptChange.update().isNarrationPending()).isTrue();
        assertThat(published.getAllValues().get(1)).isInstanceOf(NarrationRetriedFromMessageEvent.class);
    }
}
