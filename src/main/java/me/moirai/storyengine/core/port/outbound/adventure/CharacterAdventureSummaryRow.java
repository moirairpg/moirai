package me.moirai.storyengine.core.port.outbound.adventure;

import java.util.UUID;

public record CharacterAdventureSummaryRow(
        UUID publicId,
        String name,
        String imageKey,
        Double uiImagePositionX,
        Double uiImagePositionY) {
}
