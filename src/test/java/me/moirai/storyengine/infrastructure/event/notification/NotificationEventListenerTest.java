package me.moirai.storyengine.infrastructure.event.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.notification.NotificationCreated;
import me.moirai.storyengine.core.domain.notification.NotificationFixture;
import me.moirai.storyengine.core.port.inbound.notification.NotificationDetails;
import me.moirai.storyengine.core.port.outbound.notification.NotificationDetailsRow;
import me.moirai.storyengine.core.port.outbound.notification.NotificationReader;

@ExtendWith(MockitoExtension.class)
class NotificationEventListenerTest {

    private static final UUID ADVENTURE_PUBLIC_ID = UUID.randomUUID();

    @Mock
    private NotificationReader notificationReader;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationEventListener listener;

    @Test
    void shouldSendSystemNotificationToEachRecipientWhenTypeIsSystem() {

        // given
        var row = rowWith(NotificationType.SYSTEM, List.of(10L, 20L, 30L), List.of("alice", "bob", "charlie"), null);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        // when
        listener.onNotificationCreated(event);

        // then
        verify(messagingTemplate, times(3)).convertAndSendToUser(any(String.class),
                eq("/queue/notifications/system"), any(NotificationDetails.class));
    }

    @Test
    void shouldDeliverOnlyTheRecipientsOwnUsernameInSystemPayload() {

        // given
        var row = rowWith(NotificationType.SYSTEM, List.of(10L, 20L, 30L), List.of("alice", "bob", "charlie"), null);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        var usernameCaptor = ArgumentCaptor.forClass(String.class);
        var payloadCaptor = ArgumentCaptor.forClass(NotificationDetails.class);

        // when
        listener.onNotificationCreated(event);

        // then
        verify(messagingTemplate, times(3)).convertAndSendToUser(
                usernameCaptor.capture(),
                eq("/queue/notifications/system"),
                payloadCaptor.capture());

        var deliveries = usernameCaptor.getAllValues();
        var payloads = payloadCaptor.getAllValues();

        for (var i = 0; i < deliveries.size(); i++) {
            assertThat(payloads.get(i).targetUsernames()).containsExactly(deliveries.get(i));
        }
    }

    @Test
    void shouldSendSingleBroadcastWhenTypeIsBroadcast() {

        // given
        var row = rowWith(NotificationType.BROADCAST, List.of(), List.of(), null);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        var payloadCaptor = ArgumentCaptor.forClass(NotificationDetails.class);

        // when
        listener.onNotificationCreated(event);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/notifications/broadcast"),
                payloadCaptor.capture());

        assertThat(payloadCaptor.getValue().targetUsernames()).isEmpty();
    }

    @Test
    void shouldSendAdventureNotificationWhenTypeIsGame() {

        // given
        var row = rowWith(NotificationType.GAME, List.of(), List.of(), ADVENTURE_PUBLIC_ID);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        // when
        listener.onNotificationCreated(event);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(
                eq("/topic/notifications/adventure/" + ADVENTURE_PUBLIC_ID),
                any(NotificationDetails.class));
    }

    @Test
    void shouldThrowNotFoundWhenAnyRecipientUserMissing() {

        // given
        var row = rowWith(NotificationType.SYSTEM, List.of(10L, 20L), List.of("alice"), null);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        // then
        assertThatThrownBy(() -> listener.onNotificationCreated(event))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowNotFoundWhenTheNotificationIsGone() {

        // given
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> listener.onNotificationCreated(event))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void shouldThrowNotFoundWhenAGameNotificationHasNoAdventure() {

        // given
        var row = rowWith(NotificationType.GAME, List.of(), List.of(), null);
        var event = new NotificationCreated(NotificationFixture.PUBLIC_ID);

        when(notificationReader.getNotificationByPublicId(eq(NotificationFixture.PUBLIC_ID)))
                .thenReturn(Optional.of(row));

        // then
        assertThatThrownBy(() -> listener.onNotificationCreated(event))
                .isInstanceOf(NotFoundException.class);
    }

    private NotificationDetailsRow rowWith(
            NotificationType type,
            List<Long> recipientUserIds,
            List<String> recipientUsernames,
            UUID adventureId) {

        return new NotificationDetailsRow(
                NotificationFixture.PUBLIC_ID,
                "System message",
                type,
                NotificationLevel.INFO,
                recipientUserIds,
                recipientUsernames,
                adventureId,
                false,
                Map.of(),
                Instant.now(),
                Instant.now());
    }
}
