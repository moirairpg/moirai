package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.message.MessageSentEvent;
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.inbound.message.SendMessage;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.adventure.EnrolledCharacterData;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class SendMessageHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SendMessageHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenContentIsBlank() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "   ", "user");

        // when / then
        assertThrows(IllegalArgumentException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldSaveThePlayerMessageUnderTheirCharacterNameWhenTheyAreEnrolled() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        givenAdventureExists();
        givenEnrolledCharacter();

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());

        assertThat(saved.getValue().getContent()).startsWith("Aria said");
        assertThat(saved.getValue().getRole()).isEqualTo(MessageAuthorRole.USER);
    }

    @Test
    public void shouldRecordTheAuthorWhenThePlayerIsEnrolled() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        givenAdventureExists();
        givenEnrolledCharacter();

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());

        assertThat(saved.getValue().getAuthorId()).isEqualTo(1111L);
        assertThat(saved.getValue().getAuthorCharacterId()).isEqualTo(4L);
        assertThat(saved.getValue().getAuthorCharacterName()).isEqualTo("Aria");
    }

    @Test
    public void shouldRecordNoAuthorWhenThePlayerIsNotEnrolled() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        givenAdventureExists();

        when(adventureRepository.findEnrolledCharacter(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when
        handler.handle(command);

        // then
        var saved = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(saved.capture());

        assertThat(saved.getValue().getAuthorId()).isNull();
        assertThat(saved.getValue().getAuthorCharacterId()).isNull();
        assertThat(saved.getValue().getAuthorCharacterName()).isNull();
    }

    @Test
    public void shouldPublishTheAddedMessageBeforeTheFlowEventWhenAMessageIsSent() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        givenAdventureExists();

        when(adventureRepository.findEnrolledCharacter(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(published.capture());

        assertThat(published.getAllValues().get(0)).isInstanceOf(MessageTranscriptChangedEvent.class);
        assertThat(published.getAllValues().get(1)).isInstanceOf(MessageSentEvent.class);
    }

    @Test
    public void shouldMarkNarrationAsPendingWhenAMessageIsSent() {

        // given
        var command = new SendMessage(UUID.randomUUID(), "Hello!", "user");

        givenAdventureExists();

        when(adventureRepository.findEnrolledCharacter(anyLong(), anyString()))
                .thenReturn(Optional.empty());

        // when
        handler.handle(command);

        // then
        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getAllValues().get(0);

        assertThat(transcriptChange.update().change()).isEqualTo(TranscriptChange.MESSAGE_ADDED);
        assertThat(transcriptChange.update().isNarrationPending()).isTrue();
    }

    private void givenEnrolledCharacter() {

        when(adventureRepository.findEnrolledCharacter(anyLong(), anyString()))
                .thenReturn(Optional.of(new EnrolledCharacterData(1111L, 4L, "Aria")));
    }

    private void givenAdventureExists() {

        var adventure = AdventureFixture.privateAdventureWithId();
        var savedMessage = MessageFixture.userMessage().build();

        ReflectionTestUtils.setField(savedMessage, "publicId", UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.of(adventure));
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
    }
}
