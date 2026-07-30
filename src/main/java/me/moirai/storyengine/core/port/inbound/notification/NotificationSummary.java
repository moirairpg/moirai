package me.moirai.storyengine.core.port.inbound.notification;

import java.time.Instant;
import java.util.UUID;

import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;

public record NotificationSummary(
        UUID publicId,
        String message,
        NotificationType type,
        NotificationLevel level,
        Instant creationDate) {
}
