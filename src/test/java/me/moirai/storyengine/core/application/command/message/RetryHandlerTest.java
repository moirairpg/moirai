package me.moirai.storyengine.core.application.command.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import me.moirai.storyengine.core.application.event.message.MessageTranscriptChangedEvent;
import me.moirai.storyengine.core.application.event.message.NarrationRetriedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.inbound.message.Retry;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class RetryHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private RetryHandler handler;

    @Test
    public void shouldThrowExceptionWhenAdventureIsNotFound() {

        // given
        var command = new Retry(UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when / then
        assertThrows(NotFoundException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenAdventureHasNoMessages() {

        // given
        var command = new Retry(UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.getLastActive(anyLong())).thenReturn(Optional.empty());

        // when / then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldThrowExceptionWhenTheLastMessageIsNotAnAiResponse() {

        // given
        var command = new Retry(UUID.randomUUID());
        var lastMessage = MessageFixture.userMessage().build();

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.getLastActive(anyLong())).thenReturn(Optional.of(lastMessage));

        // when / then
        assertThrows(BusinessRuleViolationException.class, () -> handler.handle(command));
    }

    @Test
    public void shouldRemoveTheLastNarrationAndMarkNarrationAsPendingWhenRetrying() {

        // given
        var command = new Retry(UUID.randomUUID());
        var lastMessage = MessageFixture.assistantMessage().build();
        var lastMessageId = UUID.randomUUID();

        ReflectionTestUtils.setField(lastMessage, "publicId", lastMessageId);

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(messageRepository.getLastActive(anyLong())).thenReturn(Optional.of(lastMessage));

        // when
        handler.handle(command);

        // then
        verify(messageRepository).deleteLastAssistantMessage(anyLong());

        var published = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(2)).publishEvent(published.capture());

        var transcriptChange = (MessageTranscriptChangedEvent) published.getAllValues().get(0);

        assertThat(transcriptChange.update().change()).isEqualTo(TranscriptChange.MESSAGE_REMOVED);
        assertThat(transcriptChange.update().messageId()).isEqualTo(lastMessageId);
        assertThat(transcriptChange.update().isNarrationPending()).isTrue();
        assertThat(published.getAllValues().get(1)).isInstanceOf(NarrationRetriedEvent.class);
    }
}
