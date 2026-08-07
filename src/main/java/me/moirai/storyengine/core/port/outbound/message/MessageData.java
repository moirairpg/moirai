package me.moirai.storyengine.core.port.outbound.message;

import java.time.Instant;
import java.util.UUID;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessageStatus;

public record MessageData(
        UUID publicId,
        Long adventureId,
        UUID authorId,
        String authorCharacterName,
        MessageAuthorRole role,
        String content,
        Instant creationDate,
        MessageStatus status) {
}
