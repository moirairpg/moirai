package me.moirai.storyengine.core.application.event.notification;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.notification.NotificationFixture;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.userdetails.UserFixture;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class NotificationDomainEventListenerTest {

    private static final Long DELETED_USER_ID = UserFixture.NUMERIC_ID;
    private static final Long ANOTHER_USER_ID = 9999L;

    @Mock
    private NotificationRepository notificationRepository;

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
