package me.moirai.storyengine.core.port.outbound.notification;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;
import me.moirai.storyengine.common.util.Functions;

public record NotificationDetailsRow(
        UUID publicId,
        String message,
        NotificationType type,
        NotificationLevel level,
        List<Long> recipientUserIds,
        List<String> recipientUsernames,
        UUID adventureId,
        boolean isInteractable,
        Map<String, Object> metadata,
        Instant creationDate,
        Instant lastUpdateDate) {

    public NotificationDetailsRow {
        recipientUserIds = Functions.mapOrDefault(recipientUserIds, List.of(), Collections::unmodifiableList);
        recipientUsernames = Functions.mapOrDefault(recipientUsernames, List.of(), Collections::unmodifiableList);
    }
}
