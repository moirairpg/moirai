package me.moirai.storyengine.core.application.event.character;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.outbound.character.PlayerCharacterRepository;

@ExtendWith(MockitoExtension.class)
public class CharacterDomainEventListenerTest {

    private static final UUID FIRST_CHARACTER = UUID.fromString("857345aa-4444-0000-0000-000000000001");
    private static final UUID SECOND_CHARACTER = UUID.fromString("857345aa-4444-0000-0000-000000000002");

    @Mock
    private PlayerCharacterRepository playerCharacterRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CharacterDomainEventListener listener;

    @Test
    void shouldDeleteEveryCharacterOwnedByTheUserWhenTheUserIsDeleted() {

        // given
        when(playerCharacterRepository.findAllByPlayerId(UserFixture.NUMERIC_ID))
                .thenReturn(List.of(characterWithPublicId(FIRST_CHARACTER), characterWithPublicId(SECOND_CHARACTER)));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(playerCharacterRepository).deleteByPublicId(FIRST_CHARACTER);
        verify(playerCharacterRepository).deleteByPublicId(SECOND_CHARACTER);
    }

    @Test
    void shouldAnnounceEveryCharacterDeletionWhenTheUserIsDeleted() {

        // given
        when(playerCharacterRepository.findAllByPlayerId(UserFixture.NUMERIC_ID))
                .thenReturn(List.of(characterWithPublicId(FIRST_CHARACTER), characterWithPublicId(SECOND_CHARACTER)));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        var publishedEvent = ArgumentCaptor.forClass(PlayerCharacterDeletedEvent.class);
        verify(eventPublisher, times(2)).publishEvent(publishedEvent.capture());

        assertThat(publishedEvent.getAllValues())
                .extracting(PlayerCharacterDeletedEvent::getPublicId)
                .containsExactly(FIRST_CHARACTER, SECOND_CHARACTER);
    }

    @Test
    void shouldDoNothingWhenTheUserOwnsNoCharacters() {

        // given
        when(playerCharacterRepository.findAllByPlayerId(UserFixture.NUMERIC_ID)).thenReturn(emptyList());

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(playerCharacterRepository, never()).deleteByPublicId(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private PlayerCharacter characterWithPublicId(UUID publicId) {

        var character = PlayerCharacterFixture.samplePlayerCharacterWithId();
        ReflectionTestUtils.setField(character, "publicId", publicId);

        return character;
    }

    private UserDeletedEvent userDeletedEvent() {

        var user = UserFixture.playerWithId();
        user.communicateUserDeleted();

        return (UserDeletedEvent) user.drainEvents().getFirst();
    }
}
