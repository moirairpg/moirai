package me.moirai.storyengine.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionOutcome;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.CharacterAttribute;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.common.enums.TranscriptChange;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.character.AttributeLevels;
import me.moirai.storyengine.core.domain.character.CharacterLeveledUpEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.character.SkillLevels;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationResult;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessagePort;
import me.moirai.storyengine.core.port.outbound.message.AdventureMessageUpdate;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

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
    private UserRepository userRepository;

    @Mock
    private RandomGenerator randomGenerator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ActionEvaluationService service;

    @Test
    public void shouldEvaluateTheActionWhenTheLastMessageIsFromAPlayer() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.assistantMessage().build(),
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var request = ArgumentCaptor.forClass(ActionEvaluationRequest.class);
        verify(actionEvaluationPort).evaluateAction(request.capture());

        assertThat(request.getValue().instructions()).isEqualTo(MessagePrompt.ACTION_EVALUATOR.getText());
        assertThat(request.getValue().messages()).hasSize(3);
        assertThat(request.getValue().messages().getFirst().content())
                .startsWith("Acting character: ")
                .contains(character.narrativeDescription());
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().change()).isEqualTo(TranscriptChange.DICE_ROLLED);
        assertThat(update.getValue().messageId()).isEqualTo(history.getLast().getPublicId());
        assertThat(update.getValue().message()).isNull();
        assertThat(update.getValue().isNarrationPending()).isTrue();

        var roll = update.getValue().roll();
        assertThat(roll.characterName()).isEqualTo("Aria");
        assertThat(roll.attribute()).isEqualTo("CHARISMA");
        assertThat(roll.attributeLevel()).isEqualTo(1);
        assertThat(roll.skill()).isEqualTo("PERSUASION");
        assertThat(roll.skillLevel()).isEqualTo(2);
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
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
    public void shouldResolveACombatSkillCheckWithItsGoverningAttribute() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "MELEE", ActionDifficulty.MEDIUM, "Real stakes."));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        var roll = update.getValue().roll();
        assertThat(roll.attribute()).isEqualTo("STRENGTH");
        assertThat(roll.attributeLevel()).isEqualTo(3);
        assertThat(roll.skillLevel()).isZero();
        assertThat(roll.modifier()).isEqualTo(3);
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
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
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).isNull();
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldDispatchTheImpossibleCardWhenTheVerdictIsImpossible() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.IMPOSSIBLE, null, "ATHLETICS", null, "Cannot be done."));

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), update.capture());

        assertThat(update.getValue().change()).isEqualTo(TranscriptChange.IMPOSSIBLE_ACTION_ATTEMPTED);
        assertThat(update.getValue().messageId()).isEqualTo(history.getLast().getPublicId());
        assertThat(update.getValue().roll()).isNull();
        assertThat(update.getValue().isNarrationPending()).isTrue();

        var impossibleAction = update.getValue().impossibleAction();
        assertThat(impossibleAction.characterName()).isEqualTo("Aria");
        assertThat(impossibleAction.attribute()).isNull();
        assertThat(impossibleAction.skill()).isEqualTo("ATHLETICS");

        assertThat(line).contains("Aria");
        assertThat(line).contains("ATHLETICS");
        assertThat(line).contains("impossible");
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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        var line = service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(line).contains("Aria");
        assertThat(line).contains("PERSUASION");
        assertThat(line).contains("failed");
    }

    @Test
    public void shouldSwallowTheFailureWhenTheActingCharacterIsNotFound() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.empty());

        // then
        assertThatCode(() -> service.evaluateLatestPlayerAction(adventure.getPublicId()))
                .doesNotThrowAnyException();

        assertThat(service.evaluateLatestPlayerAction(adventure.getPublicId())).isNull();
        verify(actionEvaluationPort, never()).evaluateAction(any());
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldRecordTheOutcomeOnThePlayerMessageWhenACheckResolves() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var playerMessage = history.getLast();

        assertThat(playerMessage.getActionOutcome()).isEqualTo(ActionOutcome.FAILURE);
        assertThat(playerMessage.getActionTarget()).isEqualTo("PERSUASION");
        verify(messageRepository, times(2)).save(playerMessage);
    }

    @Test
    public void shouldRecordTheOutcomeOnThePlayerMessageWhenTheVerdictIsImpossible() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.IMPOSSIBLE, null, "ATHLETICS", null, "Cannot be done."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var playerMessage = history.getLast();

        assertThat(playerMessage.getActionOutcome()).isEqualTo(ActionOutcome.IMPOSSIBLE);
        assertThat(playerMessage.getActionTarget()).isEqualTo("ATHLETICS");
    }

    @Test
    public void shouldAwardBandXpToTheActingCharacterWhenACheckSucceeds() {

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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(15);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).sendToPlayer(eq("john.doe"), eq(adventure.getPublicId()), update.capture());

        assertThat(character.getXp()).isEqualTo(20);
        assertThat(history.getLast().getActionXpAwarded()).isEqualTo(20);
        assertThat(update.getValue().change()).isEqualTo(TranscriptChange.XP_GAINED);
        assertThat(update.getValue().messageId()).isEqualTo(history.getLast().getPublicId());
        assertThat(update.getValue().xpGain().amount()).isEqualTo(20);
        assertThat(update.getValue().xpGain().total()).isEqualTo(20);
        assertThat(update.getValue().xpGain().levelUpTarget()).isEqualTo(100);

        var topicUpdate = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), topicUpdate.capture());
        assertThat(topicUpdate.getValue().change()).isEqualTo(TranscriptChange.DICE_ROLLED);
    }

    @Test
    public void shouldAwardHalfXpWhenACheckFails() {

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
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var update = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort).sendToPlayer(eq("john.doe"), eq(adventure.getPublicId()), update.capture());

        assertThat(character.getXp()).isEqualTo(10);
        assertThat(update.getValue().xpGain().amount()).isEqualTo(10);
    }

    @Test
    public void shouldAwardTheBaseTierXpWhenTheOutcomeIsCritical() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.FORMIDABLE, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(20);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(character.getXp()).isEqualTo(50);
    }

    @Test
    public void shouldAwardNothingWhenTheCharacterIsFullyTrained() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        ReflectionTestUtils.setField(character, "attributeLevels", new AttributeLevels(5, 5, 5, 5, 5, 5));
        ReflectionTestUtils.setField(character, "skillLevels", new SkillLevels(
                4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4));

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(randomGenerator.nextInt(1, 21)).thenReturn(10);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(character.getXp()).isZero();
        verify(adventureMessagePort, never()).sendToPlayer(any(), any(), any());
        verify(adventureMessagePort).send(eq(adventure.getPublicId()), any());
    }

    @Test
    public void shouldNotAwardXpAgainWhenTheMessageAlreadyPaid() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        character.awardXp(20);
        playerMessage.recordActionOutcome(ActionOutcome.SUCCESS, "PERSUASION");
        playerMessage.recordXpAward(20);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.HARD, "Real stakes."));
        when(randomGenerator.nextInt(1, 21)).thenReturn(15);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(character.getXp()).isEqualTo(20);
        assertThat(playerMessage.getActionXpAwarded()).isEqualTo(20);
        verify(adventureMessagePort, never()).sendToPlayer(any(), any(), any());
        verify(playerCharacterRepository, never()).save(character);
    }

    @Test
    public void shouldKeepTheXpMemoWhenTheOutcomeIsCleared() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        playerMessage.recordActionOutcome(ActionOutcome.SUCCESS, "PERSUASION");
        playerMessage.recordXpAward(20);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(playerMessage.getActionOutcome()).isNull();
        assertThat(playerMessage.getActionXpAwarded()).isEqualTo(20);
        assertThat(character.getXp()).isZero();
    }

    @Test
    public void shouldDispatchThePublicLevelUpCardAndTheNotificationEventWhenACharacterLevelsUp() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        character.awardXp(90);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "PERSUASION", ActionDifficulty.MEDIUM, "Real stakes."));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(userRepository.findById(PlayerCharacterFixture.PLAYER_ID)).thenReturn(Optional.of(UserFixture.playerWithId()));
        when(randomGenerator.nextInt(1, 21)).thenReturn(15);

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        var updates = ArgumentCaptor.forClass(AdventureMessageUpdate.class);
        verify(adventureMessagePort, times(2)).send(eq(adventure.getPublicId()), updates.capture());

        var levelUpUpdate = updates.getAllValues().getLast();
        assertThat(levelUpUpdate.change()).isEqualTo(TranscriptChange.LEVEL_UP);
        assertThat(levelUpUpdate.levelUp().characterName()).isEqualTo("Volin Habar");
        assertThat(levelUpUpdate.levelUp().newLevel()).isEqualTo(2);
        assertThat(levelUpUpdate.levelUp().attributePoints()).isEqualTo(1);
        assertThat(levelUpUpdate.levelUp().skillPoints()).isEqualTo(2);

        assertThat(character.getLevel()).isEqualTo(2);
        verify(eventPublisher).publishEvent(any(CharacterLeveledUpEvent.class));
    }

    @Test
    public void shouldClearTheRecordWhenTheVerdictIsNoCheck() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        playerMessage.recordActionOutcome(ActionOutcome.SUCCESS, "PERSUASION");
        playerMessage.recordXpAward(20);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.NO_CHECK, null, null, null, "Trivial."));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(playerMessage.getActionOutcome()).isNull();
        assertThat(playerMessage.getActionTarget()).isNull();
        verify(messageRepository).save(playerMessage);
    }

    @Test
    public void shouldClearTheRecordWhenRpgMechanicsAreOff() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        adventure.updateRpgMechanicsEnabled(false);
        playerMessage.recordActionOutcome(ActionOutcome.SUCCESS, "PERSUASION");
        playerMessage.recordXpAward(20);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(playerMessage.getActionOutcome()).isNull();
        verify(actionEvaluationPort, never()).evaluateAction(any());
        verify(messageRepository).save(playerMessage);
    }

    @Test
    public void shouldClearTheRecordWhenTheEvaluationThrows() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        playerMessage.recordActionOutcome(ActionOutcome.SUCCESS, "PERSUASION");
        playerMessage.recordXpAward(20);

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
        when(actionEvaluationPort.evaluateAction(any())).thenThrow(new RuntimeException("model unavailable"));

        // when
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
        assertThat(playerMessage.getActionOutcome()).isNull();
        assertThat(playerMessage.getActionTarget()).isNull();
    }

    @Test
    public void shouldRecallTheRecordedOutcomeLineWhenTheLastMessageCarriesOne() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var playerMessage = MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build();

        playerMessage.recordActionOutcome(ActionOutcome.FAILURE, "PERSUASION");

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(List.of(playerMessage));

        // when
        var line = service.recallRecordedOutcome(adventure.getPublicId());

        // then
        assertThat(line).contains("Aria");
        assertThat(line).contains("PERSUASION");
        assertThat(line).contains("failed");
        verify(actionEvaluationPort, never()).evaluateAction(any());
        verify(adventureMessagePort, never()).send(any(), any());
    }

    @Test
    public void shouldRecallNothingWhenTheLastMessageIsFromTheAssistant() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build(), MessageFixture.assistantMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);

        // when
        var line = service.recallRecordedOutcome(adventure.getPublicId());

        // then
        assertThat(line).isNull();
    }

    @Test
    public void shouldRecallNothingWhenTheLastMessageHasNoRecord() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);

        // when
        var line = service.recallRecordedOutcome(adventure.getPublicId());

        // then
        assertThat(line).isNull();
    }

    @Test
    public void shouldRecallNothingWhenRpgMechanicsAreOff() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var playerMessage = MessageFixture.userMessage().build();

        adventure.updateRpgMechanicsEnabled(false);
        playerMessage.recordActionOutcome(ActionOutcome.FAILURE, "PERSUASION");

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));

        // when
        var line = service.recallRecordedOutcome(adventure.getPublicId());

        // then
        assertThat(line).isNull();
    }

    @Test
    public void shouldSwallowTheFailureWhenTheEvaluationThrows() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var history = List.of(
                MessageFixture.userMessage().authorCharacterId(PlayerCharacterFixture.NUMERIC_ID).build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(playerCharacterRepository.findById(PlayerCharacterFixture.NUMERIC_ID)).thenReturn(Optional.of(character));
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
