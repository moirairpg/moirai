package me.moirai.storyengine.core.application.event.notification;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.domain.Permission;
import me.moirai.storyengine.common.enums.NotificationKind;
import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessGrantedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessLevelChangedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessRevokedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.notification.NotificationFixture;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.domain.world.WorldAccessGrantedEvent;
import me.moirai.storyengine.core.domain.world.WorldAccessLevelChangedEvent;
import me.moirai.storyengine.core.domain.world.WorldAccessRevokedEvent;
import me.moirai.storyengine.core.domain.world.WorldFixture;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationDomainEventListenerTest {

    private static final Long DELETED_USER_ID = UserFixture.NUMERIC_ID;
    private static final Long ANOTHER_USER_ID = 9999L;
    private static final Long TARGET_USER_ID = 4444L;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private NotificationDomainEventListener listener;

    @Test
    void shouldDropTheRecipientAndKeepTheNotificationWhenOtherRecipientsRemain() {

        // given
        var notification = systemNotificationFor(DELETED_USER_ID, ANOTHER_USER_ID);
        notification.markAsRead(DELETED_USER_ID);

        when(notificationRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(List.of(notification));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(notificationRepository).save(notification);
        verify(notificationRepository, never()).deleteByPublicId(any());

        assertThat(notification.getRecipientUserIds()).containsExactly(ANOTHER_USER_ID);
        assertThat(notification.getReadDate(DELETED_USER_ID)).isEmpty();
    }

    @Test
    void shouldDeleteTheNotificationWhenTheDeletedUserWasItsOnlyRecipient() {

        // given
        var notification = systemNotificationFor(DELETED_USER_ID);

        when(notificationRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(List.of(notification));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(notificationRepository).deleteByPublicId(NotificationFixture.PUBLIC_ID);
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void shouldKeepABroadcastWhenTheDeletedUserHadOnlyReadIt() {

        // given
        var notification = NotificationFixture.broadcastWithId();
        notification.markAsRead(DELETED_USER_ID);

        when(notificationRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(List.of(notification));

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(notificationRepository).save(notification);
        verify(notificationRepository, never()).deleteByPublicId(any());

        assertThat(notification.getReadDate(DELETED_USER_ID)).isEmpty();
    }

    @Test
    void shouldDoNothingWhenNoNotificationInvolvesTheUser() {

        // given
        when(notificationRepository.findAllInvolving(DELETED_USER_ID)).thenReturn(emptyList());

        // when
        listener.onUserDeleted(userDeletedEvent());

        // then
        verify(notificationRepository, never()).save(any());
        verify(notificationRepository, never()).deleteByPublicId(any());
    }

    @Test
    void shouldNotifyTheTargetUserWhenWorldAccessIsGranted() {

        // given
        var event = worldAccessGrantedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onWorldAccessGranted(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("You were given read access to the world MoirAI");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getAdventureId()).isNull();
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.WORLD_ACCESS_GRANTED.name())
                .containsEntry("worldId", WorldFixture.PUBLIC_ID.toString())
                .containsEntry("worldName", "MoirAI")
                .containsEntry("level", PermissionLevel.READ.name());

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    @Test
    void shouldNotifyTheTargetUserWhenWorldAccessLevelIsChanged() {

        // given
        var event = worldAccessLevelChangedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onWorldAccessLevelChanged(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("Your access to the world MoirAI was changed to write");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.WORLD_ACCESS_LEVEL_CHANGED.name())
                .containsEntry("worldId", WorldFixture.PUBLIC_ID.toString())
                .containsEntry("worldName", "MoirAI")
                .containsEntry("level", PermissionLevel.WRITE.name());

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    @Test
    void shouldNotifyTheTargetUserWhenWorldAccessIsRevoked() {

        // given
        var event = worldAccessRevokedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onWorldAccessRevoked(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("Your access to the world MoirAI was revoked");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.WORLD_ACCESS_REVOKED.name())
                .containsEntry("worldId", WorldFixture.PUBLIC_ID.toString())
                .containsEntry("worldName", "MoirAI")
                .doesNotContainKey("level");

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    @Test
    void shouldNotifyTheTargetUserWhenAdventureAccessIsGranted() {

        // given
        var event = adventureAccessGrantedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onAdventureAccessGranted(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("You were given read access to the adventure Name");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getAdventureId()).isEqualTo(AdventureFixture.NUMERIC_ID);
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.ADVENTURE_ACCESS_GRANTED.name())
                .containsEntry("adventureId", AdventureFixture.PUBLIC_ID.toString())
                .containsEntry("adventureName", "Name")
                .containsEntry("level", PermissionLevel.READ.name());

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    @Test
    void shouldNotifyTheTargetUserWhenAdventureAccessLevelIsChanged() {

        // given
        var event = adventureAccessLevelChangedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onAdventureAccessLevelChanged(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("Your access to the adventure Name was changed to write");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getAdventureId()).isEqualTo(AdventureFixture.NUMERIC_ID);
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.ADVENTURE_ACCESS_LEVEL_CHANGED.name())
                .containsEntry("adventureId", AdventureFixture.PUBLIC_ID.toString())
                .containsEntry("adventureName", "Name")
                .containsEntry("level", PermissionLevel.WRITE.name());

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    @Test
    void shouldNotifyTheTargetUserWhenAdventureAccessIsRevoked() {

        // given
        var event = adventureAccessRevokedEvent();

        when(notificationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        listener.onAdventureAccessRevoked(event);

        // then
        var notification = captureSavedNotification();

        assertThat(notification.getMessage()).isEqualTo("Your access to the adventure Name was revoked");
        assertThat(notification.getRecipientUserIds()).containsExactly(TARGET_USER_ID);
        assertThat(notification.getAdventureId()).isEqualTo(AdventureFixture.NUMERIC_ID);
        assertThat(notification.getMetadata())
                .containsEntry("kind", NotificationKind.ADVENTURE_ACCESS_REVOKED.name())
                .containsEntry("adventureId", AdventureFixture.PUBLIC_ID.toString())
                .containsEntry("adventureName", "Name")
                .doesNotContainKey("level");

        assertNonInteractableSystemInfo(notification);

        verify(eventPublisher).publishEvent(new NotificationCreatedEvent(notification.getPublicId()));
    }

    private Notification captureSavedNotification() {

        var savedNotification = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(savedNotification.capture());

        return savedNotification.getValue();
    }

    private void assertNonInteractableSystemInfo(Notification notification) {

        assertThat(notification.getType()).isEqualTo(NotificationType.SYSTEM);
        assertThat(notification.getLevel()).isEqualTo(NotificationLevel.INFO);
        assertThat(notification.isInteractable()).isFalse();
    }

    private WorldAccessGrantedEvent worldAccessGrantedEvent() {

        var world = WorldFixture.privateWorldWithId();
        world.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));

        return (WorldAccessGrantedEvent) world.drainEvents().getFirst();
    }

    private WorldAccessLevelChangedEvent worldAccessLevelChangedEvent() {

        var world = WorldFixture.privateWorldWithId();
        world.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));
        world.drainEvents();
        world.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.WRITE)));

        return (WorldAccessLevelChangedEvent) world.drainEvents().getFirst();
    }

    private WorldAccessRevokedEvent worldAccessRevokedEvent() {

        var world = WorldFixture.privateWorldWithId();
        world.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));
        world.drainEvents();
        world.updatePermissions(Set.of());

        return (WorldAccessRevokedEvent) world.drainEvents().getFirst();
    }

    private AdventureAccessGrantedEvent adventureAccessGrantedEvent() {

        var adventure = AdventureFixture.privateAdventureWithId();
        adventure.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));

        return (AdventureAccessGrantedEvent) adventure.drainEvents().getFirst();
    }

    private AdventureAccessLevelChangedEvent adventureAccessLevelChangedEvent() {

        var adventure = AdventureFixture.privateAdventureWithId();
        adventure.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));
        adventure.drainEvents();
        adventure.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.WRITE)));

        return (AdventureAccessLevelChangedEvent) adventure.drainEvents().getFirst();
    }

    private AdventureAccessRevokedEvent adventureAccessRevokedEvent() {

        var adventure = AdventureFixture.privateAdventureWithId();
        adventure.updatePermissions(Set.of(new Permission(TARGET_USER_ID, PermissionLevel.READ)));
        adventure.drainEvents();
        adventure.updatePermissions(Set.of());

        return (AdventureAccessRevokedEvent) adventure.drainEvents().getFirst();
    }

    private Notification systemNotificationFor(Long... recipientUserIds) {

        var notification = Notification.builder()
                .message("System message")
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .recipientUserIds(List.of(recipientUserIds))
                .build();

        ReflectionTestUtils.setField(notification, "id", NotificationFixture.NUMERIC_ID);
        ReflectionTestUtils.setField(notification, "publicId", NotificationFixture.PUBLIC_ID);

        return notification;
    }

    private UserDeletedEvent userDeletedEvent() {

        var user = UserFixture.playerWithId();
        user.communicateUserDeleted();

        return (UserDeletedEvent) user.drainEvents().getFirst();
    }
}
