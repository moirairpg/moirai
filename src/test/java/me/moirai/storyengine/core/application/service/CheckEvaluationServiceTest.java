package me.moirai.storyengine.core.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import me.moirai.storyengine.common.enums.ActionDifficulty;
import me.moirai.storyengine.common.enums.ActionVerdict;
import me.moirai.storyengine.common.enums.MessagePrompt;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.message.MessageFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationPort;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationRequest;
import me.moirai.storyengine.core.port.outbound.generation.ActionEvaluationResult;
import me.moirai.storyengine.core.port.outbound.message.MessageRepository;

@ExtendWith(MockitoExtension.class)
public class CheckEvaluationServiceTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ActionEvaluationPort actionEvaluationPort;

    @InjectMocks
    private CheckEvaluationService service;

    @Test
    public void shouldEvaluateTheActionWhenTheLastMessageIsFromAPlayer() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var history = List.of(MessageFixture.assistantMessage().build(), MessageFixture.userMessage().build());

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.of(adventure));
        when(messageRepository.findAllActiveByAdventureId(adventure.getId())).thenReturn(history);
        when(actionEvaluationPort.evaluateAction(any())).thenReturn(new ActionEvaluationResult(
                ActionVerdict.CHECK, null, "STEALTH", ActionDifficulty.HARD, "Real stakes."));

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
        service.evaluateLatestPlayerAction(adventure.getPublicId());

        // then
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
    }

    @Test
    public void shouldSwallowTheFailureWhenTheAdventureIsNotFound() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();

        when(adventureRepository.findByPublicId(adventure.getPublicId())).thenReturn(Optional.empty());

        // then
        assertThatCode(() -> service.evaluateLatestPlayerAction(adventure.getPublicId()))
                .doesNotThrowAnyException();

        verify(actionEvaluationPort, never()).evaluateAction(any());
    }
}
