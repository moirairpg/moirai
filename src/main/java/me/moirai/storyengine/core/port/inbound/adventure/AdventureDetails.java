package me.moirai.storyengine.core.port.inbound.adventure;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import me.moirai.storyengine.common.enums.Moderation;
import me.moirai.storyengine.common.enums.Visibility;

public record AdventureDetails(
        UUID id,
        String name,
        String description,
        String adventureStart,
        UUID worldId,
        String narratorName,
        String narratorPersonality,
        Visibility visibility,
        Moderation moderation,
        String imageUrl,
        Instant creationDate,
        Instant lastUpdateDate,
        ModelConfigurationDto modelConfiguration,
        ContextAttributesDto contextAttributes,
        boolean canManage,
        boolean isOwner,
        Set<AdventureLorebookEntryDetails> lorebook,
        List<AdventureMembershipSummary> roster,
        Double uiImagePositionX,
        Double uiImagePositionY) {

    public AdventureDetails {
        lorebook = Set.copyOf(lorebook);
        roster = List.copyOf(roster);
    }
}
