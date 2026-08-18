package me.moirai.storyengine.core.application.event.notification;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.common.enums.NotificationKind;
import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.common.enums.PermissionLevel;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessGrantedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessLevelChangedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureAccessRevokedEvent;
import me.moirai.storyengine.core.domain.adventure.AdventureDeletedEvent;
import me.moirai.storyengine.core.domain.character.CharacterLeveledUpEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.domain.world.WorldAccessGrantedEvent;
import me.moirai.storyengine.core.domain.world.WorldAccessLevelChangedEvent;
import me.moirai.storyengine.core.domain.world.WorldAccessRevokedEvent;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@Component
public class NotificationDomainEventListener {

    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    public NotificationDomainEventListener(
            NotificationRepository notificationRepository,
            ApplicationEventPublisher eventPublisher) {

        this.notificationRepository = notificationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onAdventureDeleted(AdventureDeletedEvent event) {

        notificationRepository.deleteAllGameNotificationsByAdventureId(event.getAdventureId());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onUserDeleted(UserDeletedEvent event) {

        notificationRepository.findAllInvolving(event.getUserId())
                .forEach(notification -> withdrawUserFrom(notification, event.getUserId()));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onCharacterLeveledUp(CharacterLeveledUpEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message(event.getCharacterName() + " reached level " + event.getNewLevel())
                .isInteractable(true)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.CHARACTER_LEVEL_UP.name(),
                        "characterId", event.getCharacterPublicId().toString(),
                        "characterName", event.getCharacterName(),
                        "newLevel", event.getNewLevel()))
                .recipientUserId(event.getPlayerId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onWorldAccessGranted(WorldAccessGrantedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("You were given " + formatLevel(event.getLevel())
                        + " access to the world " + event.getWorldName())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.WORLD_ACCESS_GRANTED.name(),
                        "worldId", event.getWorldPublicId().toString(),
                        "worldName", event.getWorldName(),
                        "level", event.getLevel().name()))
                .recipientUserId(event.getUserId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onWorldAccessLevelChanged(WorldAccessLevelChangedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("Your access to the world " + event.getWorldName()
                        + " was changed to " + formatLevel(event.getLevel()))
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.WORLD_ACCESS_LEVEL_CHANGED.name(),
                        "worldId", event.getWorldPublicId().toString(),
                        "worldName", event.getWorldName(),
                        "level", event.getLevel().name()))
                .recipientUserId(event.getUserId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onWorldAccessRevoked(WorldAccessRevokedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("Your access to the world " + event.getWorldName() + " was revoked")
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.WORLD_ACCESS_REVOKED.name(),
                        "worldId", event.getWorldPublicId().toString(),
                        "worldName", event.getWorldName()))
                .recipientUserId(event.getUserId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onAdventureAccessGranted(AdventureAccessGrantedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("You were given " + formatLevel(event.getLevel())
                        + " access to the adventure " + event.getAdventureName())
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.ADVENTURE_ACCESS_GRANTED.name(),
                        "adventureId", event.getAdventurePublicId().toString(),
                        "adventureName", event.getAdventureName(),
                        "level", event.getLevel().name()))
                .recipientUserId(event.getUserId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onAdventureAccessLevelChanged(AdventureAccessLevelChangedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("Your access to the adventure " + event.getAdventureName()
                        + " was changed to " + formatLevel(event.getLevel()))
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.ADVENTURE_ACCESS_LEVEL_CHANGED.name(),
                        "adventureId", event.getAdventurePublicId().toString(),
                        "adventureName", event.getAdventureName(),
                        "level", event.getLevel().name()))
                .recipientUserId(event.getUserId())
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @EventListener
    public void onAdventureAccessRevoked(AdventureAccessRevokedEvent event) {

        publish(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message("Your access to the adventure " + event.getAdventureName() + " was revoked")
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(Map.<String, Object>of(
                        "kind", NotificationKind.ADVENTURE_ACCESS_REVOKED.name(),
                        "adventureId", event.getAdventurePublicId().toString(),
                        "adventureName", event.getAdventureName()))
                .recipientUserId(event.getUserId())
                .build());
    }

    private void withdrawUserFrom(Notification notification, Long userId) {

        notification.removeUser(userId);

        if (notification.isUndeliverable()) {
            notificationRepository.deleteByPublicId(notification.getPublicId());

            return;
        }

        notificationRepository.save(notification);
    }

    private void publish(Notification notification) {

        var saved = notificationRepository.save(notification);

        eventPublisher.publishEvent(new NotificationCreatedEvent(saved.getPublicId()));
    }

    private String formatLevel(PermissionLevel level) {
        return level.name().toLowerCase();
    }
}
