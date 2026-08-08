package me.moirai.storyengine.infrastructure.event.notification;

import java.util.Collections;
import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.notification.NotificationCreatedEvent;
import me.moirai.storyengine.core.port.inbound.notification.NotificationDetails;
import me.moirai.storyengine.core.port.outbound.notification.NotificationDetailsRow;
import me.moirai.storyengine.core.port.outbound.notification.NotificationReader;

@Component
public class NotificationEventListener {

    private static final String NOTIFICATION_NOT_FOUND = "Notification not found after creation event";
    private static final String TARGET_USER_NOT_FOUND = "Target user not found for SYSTEM notification";
    private static final String ADVENTURE_NOT_FOUND = "Adventure not found";

    private final NotificationReader notificationReader;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationEventListener(
            NotificationReader notificationReader,
            SimpMessagingTemplate messagingTemplate) {

        this.notificationReader = notificationReader;
        this.messagingTemplate = messagingTemplate;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationCreatedEvent(NotificationCreatedEvent event) {

        var notification = notificationReader.getNotificationByPublicId(event.publicId())
                .orElseThrow(() -> new NotFoundException(NOTIFICATION_NOT_FOUND));

        switch (notification.type()) {
            case BROADCAST -> sendBroadcastNotification(notification);
            case SYSTEM -> sendSystemNotification(notification);
            case GAME -> sendAdventureNotification(notification);
        }
    }

    private void sendAdventureNotification(NotificationDetailsRow notification) {

        if (notification.adventureId() == null) {
            throw new NotFoundException(ADVENTURE_NOT_FOUND);
        }

        messagingTemplate.convertAndSend(
                "/topic/notifications/adventure/" + notification.adventureId(),
                mapToDetails(notification, Collections.emptyList()));
    }

    private void sendSystemNotification(NotificationDetailsRow notification) {

        if (notification.recipientUsernames().size() != notification.recipientUserIds().size()) {
            throw new NotFoundException(TARGET_USER_NOT_FOUND);
        }

        for (var username : notification.recipientUsernames()) {
            var details = mapToDetails(notification, List.of(username));

            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/notifications/system",
                    details);
        }
    }

    private void sendBroadcastNotification(NotificationDetailsRow notification) {
        messagingTemplate.convertAndSend(
                "/topic/notifications/broadcast",
                mapToDetails(notification, Collections.emptyList()));
    }

    private NotificationDetails mapToDetails(
            NotificationDetailsRow notification,
            List<String> targetUsernames) {

        return new NotificationDetails(
                notification.publicId(),
                notification.message(),
                notification.type(),
                notification.level(),
                targetUsernames,
                notification.adventureId(),
                notification.isInteractable(),
                notification.metadata(),
                notification.creationDate(),
                notification.lastUpdateDate());
    }
}
