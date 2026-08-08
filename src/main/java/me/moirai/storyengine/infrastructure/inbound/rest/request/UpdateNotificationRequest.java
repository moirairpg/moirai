package me.moirai.storyengine.infrastructure.inbound.rest.request;

import me.moirai.storyengine.common.enums.NotificationLevel;

public record UpdateNotificationRequest(
        String message,
        NotificationLevel level) {
}
