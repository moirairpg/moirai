package me.moirai.storyengine.core.port.inbound.adventure;

import java.util.UUID;

public record CharacterAdventureSummary(UUID publicId, String name, String imageUrl) {
}
