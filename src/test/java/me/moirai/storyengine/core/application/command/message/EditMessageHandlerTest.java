package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
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
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.message.EditMessage;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@ExtendWith(MockitoExtension.class)
public class EditMessageHandlerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EditMessageHandler handler;

    @Test
    public void shouldThrowExceptionWhenContentIsBlank() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "   ");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenTheMessageIsNotFound() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content");

        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldUpdateTheContentWhenAMessageIsEdited() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content");

        givenPlayerMessageExists(UUID.randomUUID());

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());

        assertThat(saved.getValue().getContent()).isEqualTo("New content");
    }

    @Test
    public void shouldNotRemoveAnyMessageWhenAMessageIsEdited() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content");

        givenPlayerMessageExists(UUID.randomUUID());

        // when
        handler.handle(command);

        // then
        verify(messageRepository, never()).deleteNewerThanByPublicId(any(UUID.class), any(UUID.class));
        verify(messageRepository, never()).deleteByPublicId(any(UUID.class), any(UUID.class));
    }

    @Test
    public void shouldNotRequestNarrationWhenAMessageIsEdited() {

        // given
        var messageId = UUID.randomUUID();
        var command = new EditMessage(UUID.randomUUID(), messageId, "New content");

        givenPlayerMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getValue();

        assertThat(transcriptChange.update().change()).isEqualTo(TranscriptChange.MESSAGE_EDITED);
        assertThat(transcriptChange.update().messageId()).isEqualTo(messageId);
        assertThat(transcriptChange.update().isNarrationPending()).isFalse();
    }

    @Test
    public void shouldPublishTheAuthorPublicIdWhenAPlayerMessageIsEdited() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content");

        givenPlayerMessageExists(UUID.randomUUID());

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getValue();

        assertThat(transcriptChange.update().message().authorId())
                .isEqualTo(UserFixture.playerWithId().getPublicId());

        assertThat(transcriptChange.update().message().authorCharacterName()).isEqualTo("Aria");
    }

    @Test
    public void shouldPublishNoAuthorWhenANarratorMessageIsEdited() {

        // given
        var command = new EditMessage(UUID.randomUUID(), UUID.randomUUID(), "New content");

        var message = MessageFixture.assistantMessage()
                .authorCharacterName("Narrator")
                .build();

        ReflectionTestUtils.setField(message, "publicId", UUID.randomUUID());

        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getValue();

        assertThat(transcriptChange.update().message().authorId()).isNull();
        assertThat(transcriptChange.update().message().authorCharacterName()).isEqualTo("Narrator");
    }

    private void givenPlayerMessageExists(UUID messageId) {

        var message = MessageFixture.userMessage()
                .authorId(1111L)
                .authorCharacterName("Aria")
                .build();

        ReflectionTestUtils.setField(message, "publicId", messageId);

        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.of(message));
        when(messageRepository.save(any(Message.class))).thenReturn(message);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserFixture.playerWithId()));
    }
}
