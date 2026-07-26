package me.moirai.storyengine.core.application.event.adventure;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.application.event.notification.NotificationCreated;
import me.moirai.storyengine.core.domain.adventure.PlayerRemovedFromAdventureEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.notification.NotificationLevel;
import me.moirai.storyengine.core.domain.notification.NotificationType;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@Component
public class PlayerRemovedFromAdventureEventListener {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PlayerRemovedFromAdventureEventListener(
            NotificationRepository notificationRepository,
            ApplicationEventPublisher eventPublisher) {

        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPlayerRemovedFromAdventure(PlayerRemovedFromAdventureEvent event) {

        var metadata = Map.<String, Object>of(
                "kind", "ADVENTURE_MEMBER_REMOVED",
                "adventureId", event.getAdventurePublicId().toString());

        var notification = notificationRepository.save(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("You were removed from " + event.getAdventureName())
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(metadata)
                .recipientUserIds(List.of(event.getRemovedUserId()))
                .build());

        eventPublisher.publishEvent(new NotificationCreated(notification.getPublicId()));
    }
}
