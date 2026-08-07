package me.moirai.storyengine.core.application.event.notification;

import java.util.UUID;

public record NotificationCreatedEvent(UUID publicId) {
}
