package me.moirai.storyengine.core.port.inbound.message;

import java.util.UUID;

public record MessageAuthorship(UUID messageId, UUID authorId) {
}
