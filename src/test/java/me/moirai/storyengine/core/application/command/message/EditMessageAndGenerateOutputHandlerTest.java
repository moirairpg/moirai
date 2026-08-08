package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import me.moirai.storyengine.common.exception.BusinessRuleViolationException;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.message.MessageEditedEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.inbound.message.EditMessageAndGenerateOutput;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@ExtendWith(MockitoExtension.class)
public class EditMessageAndGenerateOutputHandlerTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EditMessageAndGenerateOutputHandler handler;

    @Test
    public void shouldThrowExceptionWhenContentIsBlank() {

        // given
        var command = new EditMessageAndGenerateOutput(UUID.randomUUID(), UUID.randomUUID(), "   ");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenTheMessageIsNotFound() {

        // given
        var command = new EditMessageAndGenerateOutput(UUID.randomUUID(), UUID.randomUUID(), "New content");

        when(messageRepository.getByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenTheMessageIsNotAPlayerMessage() {

        // given
        var command = new EditMessageAndGenerateOutput(UUID.randomUUID(), UUID.randomUUID(), "New content");

        when(messageRepository.getByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(MessageFixture.assistantMessage().build()));

        // when / then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldUpdateTheContentAndRemoveEverythingAfterItWhenAMessageIsEdited() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new EditMessageAndGenerateOutput(adventureId, messageId, "New content");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());
        verify(messageRepository).deleteNewerThanByPublicId(adventureId, messageId);

        assertThat(saved.getValue().getContent()).isEqualTo("New content");
    }

    @Test
    public void shouldPublishTheEditAndTheRemovalSeparatelyWhenAMessageIsEdited() {

        // given
        var messageId = UUID.randomUUID();
        var command = new EditMessageAndGenerateOutput(UUID.randomUUID(), messageId, "New content");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(3)).publishEvent(published.capture());

        var edited = (MessageTranscriptChangedEvent) published.getAllValues().get(0);
        var removed = (MessageTranscriptChangedEvent) published.getAllValues().get(1);

        assertThat(edited.update().change()).isEqualTo(TranscriptChange.MESSAGE_EDITED);
        assertThat(edited.update().messageId()).isEqualTo(messageId);
        assertThat(edited.update().message().content()).isEqualTo("New content");
        assertThat(edited.update().isNarrationPending()).isTrue();

        assertThat(removed.update().change()).isEqualTo(TranscriptChange.MESSAGES_REMOVED_AFTER);
        assertThat(removed.update().messageId()).isEqualTo(messageId);

        assertThat(published.getAllValues().get(2)).isInstanceOf(MessageEditedEvent.class);
    }

    @Test
    public void shouldKeepTheEditedMessageWhenAMessageIsEdited() {

        // given
        var adventureId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var command = new EditMessageAndGenerateOutput(adventureId, messageId, "New content");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        verify(messageRepository).deleteNewerThanByPublicId(adventureId, messageId);
        verify(messageRepository, never()).deleteByPublicId(any(UUID.class), any(UUID.class));
    }

    @Test
    public void shouldNotChangeAttributionWhenAnotherUserEditsTheMessage() {

        // given
        var messageId = UUID.randomUUID();
        var command = new EditMessageAndGenerateOutput(UUID.randomUUID(), messageId, "New content");

        givenMessageExists(messageId);

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(3)).publishEvent(published.capture());

        var edited = (MessageTranscriptChangedEvent) published.getAllValues().get(0);

        assertThat(edited.update().message().authorCharacterName()).isEqualTo("Aria");
    }

    private void givenMessageExists(UUID messageId) {

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
