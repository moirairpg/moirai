package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.message.MessageEditedEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class EditMessageHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EditMessageHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content", "user");

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenContentIsBlank() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "   ", "user");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldUpdateTheContentAndRemoveEverythingAfterItWhenAMessageIsEdited() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new EditMessage(adventureId, messageId, "New content", "user");

        givenAdventureExists();

        // when
        handler.handle(command);

        // then
        var content = ArgumentCaptor.forClass(String.class);
        verify(messageRepository).updateContent(any(UUID.class), any(UUID.class), content.capture());
        verify(messageRepository).deleteNewerThanByPublicId(adventureId, messageId);

        assertThat(content.getValue()).startsWith("Aria said");
    }

    @Test
    public void shouldThrowExceptionWhenTheMessageIsNotFound() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content", "user");

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldPublishTheEditedContentAndMarkNarrationAsPendingWhenAMessageIsEdited() {

        // given
        var messageId = UUID.randomUUID();
        var command = new EditMessage(UUID.randomUUID(), messageId, "New content", "user");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getAllValues().get(0);

        assertThat(transcriptChange.update().change()).isEqualTo(TranscriptChange.MESSAGE_EDITED);
        assertThat(transcriptChange.update().messageId()).isEqualTo(messageId);
        assertThat(transcriptChange.update().message().content()).startsWith("Aria said");
        assertThat(transcriptChange.update().isNarrationPending()).isTrue();
        assertThat(published.getAllValues().get(1)).isInstanceOf(MessageEditedEvent.class);
    }

    @Test
    public void shouldKeepTheEditedMessageWhenAMessageIsEdited() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new EditMessage(adventureId, messageId, "New content", "user");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        verify(messageRepository).deleteNewerThanByPublicId(adventureId, messageId);
        verify(messageRepository, never()).deleteByPublicId(any(UUID.class), any(UUID.class));
    }

    private void givenAdventureExists() {

        givenMessageExists(UUID.randomUUID());
    }

    private void givenMessageExists(UUID messageId) {

        var message = MessageFixture.userMessage().build();

        ReflectionTestUtils.setField(message, "publicId", messageId);

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.of(message));
        when(adventureRepository.findEnrolledCharacterName(anyLong(), anyString()))
                .thenReturn(Optional.of("Aria"));
    }
}
