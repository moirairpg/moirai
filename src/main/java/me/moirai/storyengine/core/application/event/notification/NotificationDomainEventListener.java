package me.moirai.storyengine.core.application.event.notification;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.core.domain.adventure.AdventureDeletedEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.userdetails.UserDeletedEvent;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;

@Component
public class NotificationDomainEventListener {

    private final NotificationRepository notificationRepository;

    public NotificationDomainEventListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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

    private void withdrawUserFrom(Notification notification, Long userId) {

        notification.removeUser(userId);

        if (notification.isUndeliverable()) {
            notificationRepository.deleteByPublicId(notification.getPublicId());

            return;
        }

        notificationRepository.save(notification);
    }
}
