package me.moirai.storyengine.core.application.event.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.adventure.EnrolledCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@ExtendWith(MockitoExtension.class)
public class PlayerCharacterDeletedEventListenerTest {

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PlayerCharacterDeletedEventListener listener;

    @Test
    void shouldUnenrollTheCharacterAndPublishDeletionWhenTheCharacterIsDeleted() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        var adventure = adventureContaining(character);

        when(adventureRepository.findAllContainingCharacter(character.getId()))
                .thenReturn(List.of(adventure));

        // when
        listener.onPlayerCharacterDeleted(deletionEventFor(character));

        // then
        verify(adventureRepository).save(adventure);

        var publishedEvent = ArgumentCaptor.forClass(EnrolledCharacterDeletedEvent.class);
        verify(eventPublisher).publishEvent(publishedEvent.capture());

        assertThat(publishedEvent.getValue().getPlayerId()).isEqualTo(character.getPlayerId());
        assertThat(publishedEvent.getValue().getPlayerCharacterId()).isEqualTo(character.getId());
        assertThat(publishedEvent.getValue().getAdventureId()).isEqualTo(adventure.getId());
        assertThat(publishedEvent.getValue().getAdventurePublicId()).isEqualTo(adventure.getPublicId());
        assertThat(adventure.hasCharacter(character.getId())).isFalse();
    }

    @Test
    void shouldDoNothingWhenTheCharacterBelongsToNoAdventure() {

        // given
        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();

        when(adventureRepository.findAllContainingCharacter(character.getId()))
                .thenReturn(List.of());

        // when
        listener.onPlayerCharacterDeleted(deletionEventFor(character));

        // then
        verify(adventureRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private Adventure adventureContaining(PlayerCharacter character) {

        var adventure = AdventureFixture.privateMultiplayerAdventureWithId();
        adventure.enrollPlayerCharacter(character.getId(), character.getPlayerId());
        adventure.drainEvents();

        return adventure;
    }

    private PlayerCharacterDeletedEvent deletionEventFor(PlayerCharacter character) {

        character.communicateCharacterDeleted();

        return (PlayerCharacterDeletedEvent) character.drainEvents().getFirst();
    }
}
