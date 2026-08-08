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
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.adventure.AdventureInvitationAnsweredEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.inbound.adventure.JoinAdventureWithCharacter;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;

@ExtendWith(MockitoExtension.class)
public class JoinAdventureWithCharacterHandlerTest {

    private static final Long REQUESTER_ID = 10L;

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private JoinAdventureWithCharacterHandler handler;

    private PlayerCharacter characterOwnedBy(Long playerId, Long id) {
        var character = PlayerCharacterFixture.samplePlayerCharacter().playerId(playerId).build();
        ReflectionTestUtils.setField(character, "id", id);
        return character;
    }

    @Test
    public void shouldAcceptSaveAndPublishInOrder() {

        // given
        var adventure = AdventureFixture.privateAdventureWithId();
        var invitation = adventure.invite(REQUESTER_ID, AdventureFixture.OWNER_ID);
        adventure.drainEvents();

        var character = characterOwnedBy(REQUESTER_ID, 1L);
        var command = new JoinAdventureWithCharacter(invitation.getPublicId(), character.getPublicId(), REQUESTER_ID);

        when(adventureRepository.findByInvitationPublicId(any())).thenReturn(Optional.of(adventure));
        when(playerCharacterRepository.findByPublicId(any())).thenReturn(Optional.of(character));

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
        var command = new JoinAdventureWithCharacter(UUID.randomUUID(), UUID.randomUUID(), REQUESTER_ID);

        when(adventureRepository.findByInvitationPublicId(any())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> handler.execute(command)).isInstanceOf(NotFoundException.class);
        verify(adventureRepository, never()).save(any(Adventure.class));
    }
}
