package me.moirai.storyengine.core.application.event.adventure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.core.application.event.notification.NotificationCreated;
import me.moirai.storyengine.core.domain.adventure.Adventure;
import me.moirai.storyengine.core.domain.adventure.AdventureFixture;
import me.moirai.storyengine.core.domain.adventure.PlayerRemovedFromAdventureEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PlayerRemovedFromAdventureEventListenerTest {

    private static final Long REMOVED_USER_ID = 10L;
    private static final Long REMOVER_USER_ID = 99L;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AdventureRepository adventureRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private PlayerRemovedFromAdventureEventListener listener;

    private PlayerRemovedFromAdventureEvent involuntaryRemovalEvent() {
        var adventure = AdventureFixture.privateMultiplayerAdventureWithId();
        adventure.enrollPlayerCharacter(1L, REMOVED_USER_ID);
        adventure.drainEvents();
        adventure.unenrollPlayerCharacter(REMOVED_USER_ID, REMOVER_USER_ID);
        return drain(adventure);
    }

    private PlayerRemovedFromAdventureEvent voluntaryLeaveEvent() {
        var adventure = AdventureFixture.privateMultiplayerAdventureWithId();
        adventure.enrollPlayerCharacter(1L, REMOVED_USER_ID);
        adventure.drainEvents();
        adventure.unenrollPlayerCharacter(REMOVED_USER_ID, REMOVED_USER_ID);
        return drain(adventure);
    }

    private PlayerRemovedFromAdventureEvent drain(Adventure adventure) {
        return (PlayerRemovedFromAdventureEvent) adventure.drainEvents().stream()
                .filter(PlayerRemovedFromAdventureEvent.class::isInstance)
                .findFirst()
                .orElseThrow();
    }

    private Notification savedNotification() {
        var notification = Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("stub")
                .isInteractable(false)
                .recipientUserIds(List.of(REMOVED_USER_ID))
                .build();
        ReflectionTestUtils.setField(notification, "publicId", UUID.randomUUID());
        return notification;
    }

    @Test
    public void shouldNotifyTheRemovedPlayerWhenRemovedBySomeoneElse() {

        // given
        when(notificationRepository.save(any())).thenReturn(savedNotification());

        // when
        listener.onPlayerRemovedFromAdventure(involuntaryRemovalEvent());

        // then
        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).contains("removed from");
        assertThat(captor.getValue().getMetadata()).containsEntry("kind", "ADVENTURE_MEMBER_REMOVED");
        assertThat(captor.getValue().getRecipientUserIds()).containsExactly(REMOVED_USER_ID);
        verify(eventPublisher).publishEvent(any(NotificationCreated.class));
    }

    @Test
    public void shouldNotifyManagersWhenThePlayerLeavesVoluntarily() {

        // given
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(UserFixture.player().username("bob").build()));
        when(adventureRepository.findManagerUserIdsByAdventureId(anyLong())).thenReturn(List.of(100L));
        when(notificationRepository.save(any())).thenReturn(savedNotification());

        // when
        listener.onPlayerRemovedFromAdventure(voluntaryLeaveEvent());

        // then
        var captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getMessage()).contains("bob left");
        assertThat(captor.getValue().getMetadata()).containsEntry("kind", "ADVENTURE_MEMBER_LEFT");
        assertThat(captor.getValue().getRecipientUserIds()).containsExactly(100L);
        verify(eventPublisher).publishEvent(any(NotificationCreated.class));
    }
}
