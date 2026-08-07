package me.moirai.storyengine.core.application.event.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.core.application.service.StoryContext;
import me.moirai.storyengine.core.application.service.StoryContextService;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.character.PlayerCharacterRenamedEvent;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;
import me.moirai.storyengine.core.port.outbound.generation.TextCompletionPort;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationRequest;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationResult;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class MessageDomainEventListenerTest {

    private static final UUID ADVENTURE_ID = AdventureFixture.PUBLIC_ID;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private TextCompletionPort textCompletionPort;

    @Mock
    private StoryContextService storyContextService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private MessageDomainEventListener listener;

    private MessageDomainEventListener listener() {

        return new MessageDomainEventListener(
                messageRepository,
                adventureRepository,
                textCompletionPort,
                storyContextService,
                eventPublisher,
                10);
    }

    @Test
    public void shouldOmitContinueGenerationFromInstructionsWhenAPlayerMessageWasSent() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onMessageSent(new MessageSentEvent(ADVENTURE_ID));

        // then
        assertThat(capturedInstructions()).doesNotContain(MessagePrompt.CONTINUE_GENERATION.getText());
    }

    @Test
    public void shouldIncludeContinueGenerationInInstructionsWhenTheAdventureWasStarted() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onAdventureStarted(new AdventureStartedEvent(ADVENTURE_ID));

        // then
        assertThat(capturedInstructions()).contains(MessagePrompt.CONTINUE_GENERATION.getText());
    }

    @Test
    public void shouldIncludeContinueGenerationInInstructionsWhenNarrationWasRetried() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onNarrationRetried(new NarrationRetriedEvent(ADVENTURE_ID));

        // then
        assertThat(capturedInstructions()).contains(MessagePrompt.CONTINUE_GENERATION.getText());
    }

    @Test
    public void shouldIncludeContinueGenerationInInstructionsWhenTheStoryWasContinued() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onStoryContinued(new StoryContinuedEvent(ADVENTURE_ID));

        // then
        assertThat(capturedInstructions()).contains(MessagePrompt.CONTINUE_GENERATION.getText());
    }

    @Test
    public void shouldIncludeContinueGenerationInInstructionsWhenAMessageWasEdited() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onMessageEdited(new MessageEditedEvent(ADVENTURE_ID));

        // then
        assertThat(capturedInstructions()).contains(MessagePrompt.CONTINUE_GENERATION.getText());
    }

    @Test
    public void shouldPublishTheAddedMessageWithoutMarkingNarrationAsPendingWhenNarrationSucceeds() {

        // given
        listener = listener();
        givenNarrationSucceeds();

        // when
        listener.onMessageSent(new MessageSentEvent(ADVENTURE_ID));

        // then
        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().change()).isEqualTo(TranscriptChange.MESSAGE_ADDED);
        assertThat(published.getValue().update().isNarrationPending()).isFalse();
    }

    @Test
    public void shouldPublishNarrationFailedWhenGenerationThrows() {

        // given
        listener = listener();

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(storyContextService.build(any())).thenReturn(storyContext());
        when(textCompletionPort.generateTextFrom(any())).thenThrow(new IllegalStateException("boom"));

        // when
        listener.onMessageSent(new MessageSentEvent(ADVENTURE_ID));

        // then
        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().change()).isEqualTo(TranscriptChange.NARRATION_FAILED);
        assertThat(published.getValue().update().isNarrationPending()).isFalse();
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    public void shouldPublishNarrationFailedWhenTheAdventureIsNotFound() {

        // given
        listener = listener();

        when(adventureRepository.findByPublicId(any(UUID.class))).thenReturn(Optional.empty());

        // when
        listener.onMessageSent(new MessageSentEvent(ADVENTURE_ID));

        // then
        var published = ArgumentCaptor.forClass(MessageTranscriptChangedEvent.class);
        verify(eventPublisher).publishEvent(published.capture());

        assertThat(published.getValue().update().change()).isEqualTo(TranscriptChange.NARRATION_FAILED);
    }

    @Test
    public void shouldUpdateTheRecordedNameOnTheCharactersMessagesWhenItIsRenamed() {

        // given
        listener = listener();

        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        character.updateName("Volin the Bold");

        var event = (PlayerCharacterRenamedEvent) character.drainEvents().getFirst();

        // when
        listener.onPlayerCharacterRenamed(event);

        // then
        verify(messageRepository).updateAuthorCharacterName(
                PlayerCharacterFixture.NUMERIC_ID, "Volin the Bold");
    }

    private String capturedInstructions() {

        var request = ArgumentCaptor.forClass(TextGenerationRequest.class);
        verify(textCompletionPort).generateTextFrom(request.capture());

        return request.getValue().instructions();
    }

    private void givenNarrationSucceeds() {

        var savedMessage = MessageFixture.assistantMessage().build();

        ReflectionTestUtils.setField(savedMessage, "publicId", UUID.randomUUID());

        when(adventureRepository.findByPublicId(any(UUID.class)))
                .thenReturn(Optional.of(AdventureFixture.privateAdventureWithId()));
        when(storyContextService.build(any())).thenReturn(storyContext());
        when(textCompletionPort.generateTextFrom(any()))
                .thenReturn(TextGenerationResult.builder().outputText("A door opens.").build());
        when(messageRepository.save(any(Message.class))).thenReturn(savedMessage);
    }

    private StoryContext storyContext() {

        return new StoryContext(
                List.of(ChatMessage.asUser("Hello")),
                List.of("Aria"),
                List.of());
    }
}
