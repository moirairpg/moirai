package me.moirai.storyengine.core.application.event.adventure;

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

import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureDeletedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.adventure.EnrolledCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.adventure.Invitation;
import me.moirai.storyengine.core.domain.character.PlayerCharacter;
import me.moirai.storyengine.core.domain.character.PlayerCharacterDeletedEvent;
import me.moirai.storyengine.core.domain.character.PlayerCharacterFixture;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;

@ExtendWith(MockitoExtension.class)
public class AdventureDomainEventListenerTest {

    private static final Long DELETED_USER_ID = UserFixture.NUMERIC_ID;
    private static final Long ANOTHER_USER_ID = 9999L;
    private static final Long UNRELATED_USER_ID = 8888L;
    private static final UUID FIRST_ADVENTURE = UUID.fromString("857345aa-2222-0000-0000-000000000001");
    private static final UUID SECOND_ADVENTURE = UUID.fromString("857345aa-2222-0000-0000-000000000002");

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AdventureDomainEventListener listener;

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

    @Test
    void shouldDeleteEveryAdventureOwnedByTheUserWhenTheUserIsDeleted() {

        // given
        when(adventureRepository.findAllOwnedBy(DELETED_USER_ID))
                .thenReturn(List.of(adventureWith(1L, FIRST_ADVENTURE), adventureWith(2L, SECOND_ADVENTURE)));

        when(adventureRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(emptyList());

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(adventureRepository).deleteByPublicId(FIRST_ADVENTURE);
        verify(adventureRepository).deleteByPublicId(SECOND_ADVENTURE);
        verify(adventureRepository, never()).save(any());
    }

    @Test
    void shouldAnnounceEveryAdventureDeletionWhenTheUserIsDeleted() {

        // given
        when(adventureRepository.findAllOwnedBy(DELETED_USER_ID))
                .thenReturn(List.of(adventureWith(1L, FIRST_ADVENTURE), adventureWith(2L, SECOND_ADVENTURE)));

        when(adventureRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(emptyList());

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        var publishedEvent = ArgumentCaptor.forClass(AdventureDeletedEvent.class);
        verify(eventPublisher, times(2)).publishEvent(publishedEvent.capture());

        assertThat(publishedEvent.getAllValues())
                .extracting(AdventureDeletedEvent::getPublicId)
                .containsExactly(FIRST_ADVENTURE, SECOND_ADVENTURE);
    }

    @Test
    void shouldRevokePermissionsAndWithdrawInvitationsWhenTheUserOnlyParticipates() {

        // given
        var adventure = adventureWith(1L, FIRST_ADVENTURE);
        adventure.grant(new Permission(DELETED_USER_ID, PermissionLevel.READ));
        adventure.invite(ANOTHER_USER_ID, DELETED_USER_ID);
        adventure.invite(DELETED_USER_ID, AdventureFixture.OWNER_ID);
        adventure.invite(UNRELATED_USER_ID, AdventureFixture.OWNER_ID);
        adventure.drainEvents();

        when(adventureRepository.findAllOwnedBy(DELETED_USER_ID)).thenReturn(emptyList());
        when(adventureRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(List.of(adventure));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(adventureRepository).save(adventure);
        verify(adventureRepository, never()).deleteByPublicId(any());

        assertThat(adventure.getPermissions())
                .extracting(Permission::userId)
                .doesNotContain(DELETED_USER_ID);

        assertThat(adventure.getInvitations())
                .singleElement()
                .extracting(Invitation::getUserId)
                .isEqualTo(UNRELATED_USER_ID);
    }

    @Test
    void shouldNotSaveAnAdventureThatWasJustDeletedWhenTheUserOwnsIt() {

        // given
        var owned = adventureWith(1L, FIRST_ADVENTURE);

        when(adventureRepository.findAllOwnedBy(DELETED_USER_ID)).thenReturn(List.of(owned));
        when(adventureRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(List.of(owned));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(adventureRepository).deleteByPublicId(FIRST_ADVENTURE);
        verify(adventureRepository, never()).save(any());
    }

    @Test
    void shouldDoNothingWhenTheUserHasNoAdventures() {

        // given
        when(adventureRepository.findAllOwnedBy(DELETED_USER_ID)).thenReturn(emptyList());
        when(adventureRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(emptyList());

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(adventureRepository, never()).deleteByPublicId(any());
        verify(adventureRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    private Adventure adventureContaining(PlayerCharacter character) {

        var adventure = AdventureFixture.privateAdventureWithId();
        adventure.enrollPlayerCharacter(character.getId(), character.getPlayerId());
        adventure.drainEvents();

        return adventure;
    }

    private PlayerCharacterDeletedEvent deletionEventFor(PlayerCharacter character) {

        character.communicateCharacterDeleted();

        return (PlayerCharacterDeletedEvent) character.drainEvents().getFirst();
    }

    private Adventure adventureWith(Long id, UUID publicId) {

        var adventure = AdventureFixture.privateAdventureWithId();
        ReflectionTestUtils.setField(adventure, "id", id);
        ReflectionTestUtils.setField(adventure, "publicId", publicId);

        return adventure;
    }

    private UserDeletedEvent userDeletedEvent() {

        var user = UserFixture.playerWithId();
        user.communicateUserDeleted();

        return (UserDeletedEvent) user.drainEvents().getFirst();
    }
}
