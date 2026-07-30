package me.moirai.storyengine.core.application.event.adventure;

import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.notification.NotificationCreated;
import me.moirai.storyengine.core.domain.adventure.PlayerRemovedFromAdventureEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@Component
public class PlayerRemovedFromAdventureEventListener {

    private final NotificationRepository notificationRepository;
    private final AdventureRepository adventureRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PlayerRemovedFromAdventureEventListener(
            NotificationRepository notificationRepository,
            AdventureRepository adventureRepository,
            UserRepository userRepository,
            ApplicationEventPublisher eventPublisher) {

        this.notificationRepository = notificationRepository;
        this.adventureRepository = adventureRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onPlayerRemovedFromAdventure(PlayerRemovedFromAdventureEvent event) {

        var notification = notificationRepository.save(
                event.isVoluntary() ? leftNotification(event) : removedNotification(event));

        eventPublisher.publishEvent(new NotificationCreated(notification.getPublicId()));
    }

    private Notification removedNotification(PlayerRemovedFromAdventureEvent event) {

        return Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("You were removed from " + event.getAdventureName())
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", "ADVENTURE_MEMBER_REMOVED",
                        "adventureId", event.getAdventurePublicId().toString()))
                .recipientUserIds(List.of(event.getRemovedUserId()))
                .build();
    }

    private Notification leftNotification(PlayerRemovedFromAdventureEvent event) {

        var player = userRepository.findById(event.getRemovedUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        var managerUserIds = adventureRepository.findManagerUserIdsByAdventureId(event.getAdventureId());

        return Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message(player.getUsername() + " left " + event.getAdventureName())
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", "ADVENTURE_MEMBER_LEFT",
                        "adventureId", event.getAdventurePublicId().toString(),
                        "username", player.getUsername()))
                .recipientUserIds(managerUserIds)
                .build();
    }
}
