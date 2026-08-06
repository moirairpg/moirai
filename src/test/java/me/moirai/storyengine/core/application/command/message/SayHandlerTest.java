package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.inbound.message.Say;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class SayHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SayHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // given
        var command = new Say(UUID.randomUUID(), "The door creaks open.");

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenContentIsBlank() {

        // given
        var command = new Say(UUID.randomUUID(), "   ");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldSaveTheDialogueUnderTheNarratorWhenDialogueIsInserted() {

        // given
        var command = new Say(UUID.randomUUID(), "The door creaks open.");

        givenAdventureExists();

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());

        assertThat(saved.getValue().getRole()).isEqualTo(MessageAuthorRole.ASSISTANT);
    }

    @Test
    public void shouldPublishTheAddedMessageWithoutMarkingNarrationAsPendingWhenDialogueIsInserted() {

        // given
        var command = new Say(UUID.randomUUID(), "The door creaks open.");

        givenAdventureExists();

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().change()).isEqualTo(TranscriptChange.MESSAGE_ADDED);
        assertThat(published.getValue().update().isNarrationPending()).isFalse();
    }

    private void givenAdventureExists() {

        var savedMessage = MessageFixture.assistantMessage().build();

        ReflectionTestUtils.setField(savedMessage, "publicId", UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
    }
}
