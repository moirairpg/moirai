package me.moirai.storyengine.core.application.event.adventure;

import java.util.Map;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import me.moirai.storyengine.common.enums.InvitationStatus;
import me.moirai.storyengine.common.exception.NotFoundException;
import me.moirai.storyengine.core.application.event.notification.NotificationCreated;
import me.moirai.storyengine.core.domain.adventure.AdventureInvitationAnsweredEvent;
import me.moirai.storyengine.core.domain.notification.Notification;
import me.moirai.storyengine.core.domain.notification.NotificationLevel;
import me.moirai.storyengine.core.domain.notification.NotificationType;
import me.moirai.storyengine.core.port.outbound.adventure.AdventureRepository;
import me.moirai.storyengine.core.port.outbound.notification.NotificationRepository;
import me.moirai.storyengine.core.port.outbound.userdetails.UserRepository;

@Component
public class AdventureInvitationAnsweredEventListener {

    private final NotificationRepository notificationRepository;
    private final AdventureRepository adventureRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AdventureInvitationAnsweredEventListener(
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
    public void onAdventureInvitationAnswered(AdventureInvitationAnsweredEvent event) {

        var responder = userRepository.findById(event.getRespondingUserId())
                .orElseThrow(() -> new NotFoundException("Responder not found"));

        var action = event.getResponse() == InvitationStatus.ACCEPTED ? "accepted" : "declined";

        var responseMetadata = Map.<String, Object>of(
                "kind", "ADVENTURE_INVITE_RESPONSE",
                "adventureId", event.getAdventurePublicId().toString(),
                "respondingUsername", responder.getUsername(),
                "response", event.getResponse().name());

        var managerUserIds = adventureRepository.findManagerUserIdsByAdventureId(event.getAdventureId());

        var responseNotification = notificationRepository.save(Notification.builder()
                .type(NotificationType.SYSTEM)
                .level(NotificationLevel.INFO)
                .message(responder.getUsername() + " " + action + " your invitation to " + event.getAdventureName())
                .adventureId(event.getAdventureId())
                .isInteractable(false)
                .metadata(responseMetadata)
                .recipientUserIds(managerUserIds)
                .build());

        eventPublisher.publishEvent(new NotificationCreated(responseNotification.getPublicId()));
    }
}
