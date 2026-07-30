package me.moirai.storyengine.core.port.inbound.notification;

import java.util.UUID;

import me.moirai.storyengine.common.cqs.command.Command;
import me.moirai.storyengine.common.enums.NotificationLevel;

public record UpdateNotification(
        UUID notificationId,
        String message,
        NotificationLevel level)
        implements Command<NotificationDetails> {
}
