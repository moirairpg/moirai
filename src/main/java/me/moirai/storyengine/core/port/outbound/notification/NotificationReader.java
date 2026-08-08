package me.moirai.storyengine.core.port.outbound.notification;

import java.util.Optional;
import java.util.UUID;

import me.moirai.storyengine.common.enums.Role;
import me.moirai.storyengine.core.port.inbound.notification.NotificationDetails;

public interface NotificationReader {

    Optional<NotificationDetailsRow> getNotificationByPublicId(UUID publicId);

    Optional<NotificationDetails> getNotificationByPublicIdAndRequester(
            UUID publicId, String requesterUsername, Role requesterRole);
}
