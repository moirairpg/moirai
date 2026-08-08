package me.moirai.storyengine.common.dto;

import java.time.Instant;
import java.util.UUID;

import me.moirai.storyengine.common.enums.MessageAuthorRole;
import me.moirai.storyengine.common.enums.MessageStatus;

public record MessageSummary(
        UUID id,
        MessageAuthorRole role,
        String content,
        MessageStatus status,
        UUID authorId,
        String authorCharacterName,
        Instant creationDate) {
}
