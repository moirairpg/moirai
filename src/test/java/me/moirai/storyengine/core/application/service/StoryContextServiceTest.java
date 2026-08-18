package me.moirai.storyengine.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.Message;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.outbound.adventure.ChronicleVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.adventure.LorebookVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterVectorSearchPort;
import me.moirai.storyengine.core.port.outbound.generation.ChatMessage;
import me.moirai.storyengine.core.port.outbound.generation.EmbeddingPort;
import me.moirai.storyengine.core.port.outbound.generation.TextCompletionPort;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationRequest;
import me.moirai.storyengine.core.port.outbound.generation.TextGenerationResult;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class StoryContextServiceTest {

    private static final float[] VECTOR = new float[] { 0.1f, 0.2f };

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private EmbeddingPort embeddingPort;

    @Mock
    private TextCompletionPort textCompletionPort;

    @Mock
    private LorebookVectorSearchPort lorebookVectorSearchPort;

    @Mock
    private ChronicleVectorSearchPort chronicleVectorSearchPort;

    @Mock
    private PlayerCharacterVectorSearchPort playerCharacterVectorSearchPort;

    private StoryContextService service() {

        return new StoryContextService(
                messageRepository,
                playerCharacterRepository,
                embeddingPort,
                textCompletionPort,
                lorebookVectorSearchPort,
                chronicleVectorSearchPort,
                playerCharacterVectorSearchPort,
                50, 3, 3, 3);
    }

    @Test
    public void shouldPrefixTheRecordedSpeakerWhenBuildingTheModelContext() {

        // given
        givenHistory(MessageFixture.userMessage()
                .content("I look around.")
                .authorCharacterName("Aria")
                .build());

        // when
        var context = service().assembleStoryContext(AdventureFixture.privateAdventureWithId());

        // then
        assertThat(contentsOf(context)).contains("Aria said: I look around.");
    }

    @Test
    public void shouldPrefixTheNarratorNameWhenTheMessageIsNarration() {

        // given
        givenHistory(MessageFixture.assistantMessage()
                .content("A door opens.")
                .authorCharacterName("Storyteller")
                .build());

        // when
        var context = service().assembleStoryContext(AdventureFixture.privateAdventureWithId());

        // then
        assertThat(contentsOf(context)).contains("Storyteller said: A door opens.");
    }

    @Test
    public void shouldKeepThePlayerRoleWhenPrimingTheRagExtractor() {

        // given
        givenHistory(MessageFixture.assistantMessage()
                .content("A door opens.")
                .authorCharacterName("Storyteller")
                .build());

        // when
        service().assembleStoryContext(AdventureFixture.privateAdventureWithId());

        // then
        var primed = capturedRagMessages();

        assertThat(primed.getLast().role()).isEqualTo(MessageAuthorRole.ASSISTANT);
        assertThat(primed.get(primed.size() - 2).role()).isEqualTo(MessageAuthorRole.USER);
    }

    @Test
    public void shouldInjectTheOutcomeLineAfterTheHistoryWhenOneIsProvided() {

        // given
        givenHistory(MessageFixture.userMessage()
                .content("I look around.")
                .authorCharacterName("Aria")
                .build());

        // when
        var context = service().assembleStoryContext(AdventureFixture.privateAdventureWithId(), "[Dice check: outcome]");

        // then
        var contents = contentsOf(context);

        assertThat(contents.indexOf("[Dice check: outcome]"))
                .isGreaterThan(contents.indexOf("Aria said: I look around."));
    }

    @Test
    public void shouldBuildAnIdenticalContextWhenNoOutcomeLineIsProvided() {

        // given
        givenHistory(MessageFixture.userMessage()
                .content("I look around.")
                .authorCharacterName("Aria")
                .build());

        // when
        var contextWithOutcome = service().assembleStoryContext(AdventureFixture.privateAdventureWithId(), "[Dice check: outcome]");
        var contextWithoutOutcome = service().assembleStoryContext(AdventureFixture.privateAdventureWithId());

        // then
        assertThat(contentsOf(contextWithoutOutcome)).doesNotContain("[Dice check: outcome]");
        assertThat(contextWithoutOutcome.messages()).hasSize(contextWithOutcome.messages().size() - 1);
    }

    private void givenHistory(Message message) {

        when(messageRepository.findAllActiveByAdventureId(anyLong())).thenReturn(List.of(message));
        when(messageRepository.findLatestChronicledByAdventureId(anyLong(), anyInt())).thenReturn(List.of());
        when(playerCharacterRepository.findAllByIdIn(any())).thenReturn(List.of());
        when(embeddingPort.embed(any())).thenReturn(VECTOR);
        when(lorebookVectorSearchPort.search(any(), any(), anyInt())).thenReturn(List.of());
        when(chronicleVectorSearchPort.search(any(), any(), anyInt())).thenReturn(List.of());
        when(textCompletionPort.generateTextFrom(any()))
                .thenReturn(TextGenerationResult.builder().outputText("a place").build());
    }

    private List<String> contentsOf(StoryContext context) {

        return context.messages().stream()
                .map(message -> message.content())
                .toList();
    }

    private List<ChatMessage> capturedRagMessages() {

        var request = ArgumentCaptor.forClass(TextGenerationRequest.class);

        verify(textCompletionPort).generateTextFrom(request.capture());

        return request.getValue().messages();
    }
}
