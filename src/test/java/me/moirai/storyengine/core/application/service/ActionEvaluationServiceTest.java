package me.moirai.storyengine.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.random.RandomGenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionOutcome;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationResult;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessagePort;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class ActionEvaluationServiceTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private ActionEvaluationPort actionEvaluationPort;

    @Mock
    private AdventureMessagePort adventureMessagePort;

    @Mock
    private RandomGenerator randomGenerator;

    @InjectMocks
    private ActionEvaluationService service;

    @Test
    public void shouldEvaluateTheActionWhenTheLastMessageIsFromAPlayer() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.assistantMessage().build(), MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var request = ArgumentCaptor.forClass(ActionEvaluationRequest.class);
        verify(actionEvaluationPort).evaluateAction(request.capture());

        assertThat(request.getValue().instructions()).isEqualTo(MessagePrompt.ACTION_EVALUATOR.getText());
        assertThat(request.getValue().messages()).hasSize(2);
    }

    @Test
    public void shouldNotEvaluateWhenRpgMechanicsAreOff() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();

        adventure.updateRpgMechanicsEnabled(false);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).isNull();
        verify(actionEvaluationPort, never()).evaluateAction(any());
    }

    @Test
    public void shouldNotEvaluateWhenTheLastMessageIsNotFromAPlayer() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build(), MessageFixture.assistantMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        verify(actionEvaluationPort, never()).evaluateAction(any());
    }

    @Test
    public void shouldNotEvaluateWhenTheAdventureHasNoMessages() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(anyLong())).thenReturn(List.of());

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        verify(actionEvaluationPort, never()).evaluateAction(any());
    }

    @Test
    public void shouldDispatchTheRollWhenTheVerdictIsACheck() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.assistantMessage().build(),
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().change()).isEqualTo(TranscriptChange.DICE_ROLLED);
        assertThat(update.getValue().messageId()).isNull();
        assertThat(update.getValue().message()).isNull();
        assertThat(update.getValue().isNarrationPending()).isTrue();

        var roll = update.getValue().roll();
        assertThat(roll.characterName()).isEqualTo("Aria");
        assertThat(roll.attribute()).isNull();
        assertThat(roll.skill()).isEqualTo("PERSUASION");
        assertThat(roll.difficulty()).isEqualTo(ActionDifficulty.HARD);
        assertThat(roll.dc()).isEqualTo(16);
        assertThat(roll.naturalRoll()).isEqualTo(10);
        assertThat(roll.modifier()).isEqualTo(3);
        assertThat(roll.total()).isEqualTo(13);
        assertThat(roll.outcome()).isEqualTo(ActionOutcome.FAILURE);
    }

    @Test
    public void shouldTierACriticalFailureWhenTheNaturalRollIsOne() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, CharacterAttribute.STRENGTH, null, ActionDifficulty.EASY, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(1);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().roll().outcome()).isEqualTo(ActionOutcome.CRITICAL_FAILURE);
    }

    @Test
    public void shouldTierACriticalSuccessWhenTheNaturalRollIsTwenty() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, CharacterAttribute.INTELLIGENCE, null, ActionDifficulty.FORMIDABLE,
                "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(20);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().roll().total()).isEqualTo(20);
        assertThat(update.getValue().roll().outcome()).isEqualTo(ActionOutcome.CRITICAL_SUCCESS);
    }

    @Test
    public void shouldUseTheSignatureLevelWhenTheCheckNamesTheOwnSignature() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "ZEAL", ActionDifficulty.MEDIUM, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().roll().modifier()).isEqualTo(2);
    }

    @Test
    public void shouldUseOnlyTheAttributeWhenTheCheckNamesAnotherClassSignature() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "HEX", ActionDifficulty.MEDIUM, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().roll().modifier()).isZero();
    }

    @Test
    public void shouldUseOnlyTheAttributeWhenTheCheckNamesAnAttribute() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, CharacterAttribute.STRENGTH, null, ActionDifficulty.MEDIUM, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().roll().modifier()).isEqualTo(3);
    }

    @Test
    public void shouldNotDispatchWhenTheVerdictIsNoCheck() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).isNull();
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldNotDispatchWhenTheVerdictIsImpossible() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.IMPOSSIBLE, null, null, null, "Cannot be done."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldReturnTheTierOutcomeLineWhenACheckResolves() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).contains("Aria");
        assertThat(line).contains("PERSUASION");
        assertThat(line).contains("failed");
    }

    @Test
    public void shouldReturnTheImpossibleLineWithoutDispatchingWhenTheVerdictIsImpossible() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.IMPOSSIBLE, null, null, null, "Cannot be done."));

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).contains("Aria");
        assertThat(line).contains("impossible");
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldSwallowTheFailureWhenTheActingCharacterIsNotFound() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.empty());

        // then
        assertThatCode(() -> service.evaluateLatestPlayerAction(adventure.getPublicId()))
                .doesNotThrowAnyException();

        assertThat(service.evaluateLatestPlayerAction(adventure.getPublicId())).isNull();
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldSwallowTheFailureWhenTheEvaluationThrows() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenThrow(new RuntimeException("model unavailable"));

        // then
        assertThatCode(() -> service.evaluateLatestPlayerAction(adventure.getPublicId()))
                .doesNotThrowAnyException();

        assertThat(service.evaluateLatestPlayerAction(adventure.getPublicId())).isNull();
    }

    @Test
    public void shouldSwallowTheFailureWhenTheAdventureIsNotFound() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.empty());

        // then
        assertThatCode(() -> service.evaluateLatestPlayerAction(adventure.getPublicId()))
                .doesNotThrowAnyException();

        assertThat(service.evaluateLatestPlayerAction(adventure.getPublicId())).isNull();
        verify(actionEvaluationPort, never()).evaluateAction(any());
    }
}
