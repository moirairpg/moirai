package me.moirai.storyengine.core.application.command.adventure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.adventure.AdventureInvitationAnsweredEvent;
import me.moirai.storyengine.core.port.inbound.adventure.DeclineAdventureInvitation;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@ExtendWith(MockitoExtension.class)
public class DeclineAdventureInvitationHandlerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private DeclineAdventureInvitationHandler handler;

    @Test
    public void shouldDeclineSaveAndPublishInOrder() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var invitation = adventure.invite(10L, AdventureFixture.OWNER_ID);
        adventure.drainEvents();

        var command = new DeclineAdventureInvitation(invitation.getPublicId());

        when(adventureRepository.findByInvitationPublicId(any())).thenReturn(Optional.of(adventure));

        // when
        handler.execute(command);

        // then
        InOrder inOrder = inOrder(adventureRepository, eventPublisher);
        inOrder.verify(adventureRepository).save(adventure);
        inOrder.verify(eventPublisher).publishEvent(any(AdventureInvitationAnsweredEvent.class));
    }

    @Test
    public void shouldThrowWhenInvitationIsMissing() {

        // given
        var command = new DeclineAdventureInvitation(UUID.randomUUID());

        when(adventureRepository.findByInvitationPublicId(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> handler.execute(command)).isInstanceOf(NotFoundException.class);
        verify(adventureRepository, never()).save(any(Adventure.class));
    }
}
