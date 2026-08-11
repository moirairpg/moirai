package me.moirai.storyengine.core.port.inbound.world;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record WorldDetails(
        UUID id,
        String name,
        String description,
        String adventureStart,
        String narratorName,
        String narratorPersonality,
        String visibility,
        String imageUrl,
        boolean canManage,
        boolean isOwner,
        Set<WorldLorebookEntryDetails> lorebook,
        Instant creationDate,
        Instant lastUpdateDate,
        Double uiImagePositionX,
        Double uiImagePositionY) {

    public WorldDetails {
        lorebook = Set.copyOf(lorebook);
    }
}
