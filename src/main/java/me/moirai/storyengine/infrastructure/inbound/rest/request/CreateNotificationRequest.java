package me.moirai.storyengine.infrastructure.inbound.rest.request;

import java.util.List;
import java.util.Map;

import me.moirai.storyengine.common.enums.NotificationLevel;
import me.moirai.storyengine.common.enums.NotificationType;

public record CreateNotificationRequest(
        String message,
        NotificationType type,
        NotificationLevel level,
        List<String> targetUsernames,
        boolean isInteractable,
        Map<String, Object> metadata) {
}
