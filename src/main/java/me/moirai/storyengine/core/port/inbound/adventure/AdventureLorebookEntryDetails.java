package me.moirai.storyengine.core.port.inbound.adventure;

import java.time.Instant;
import java.util.UUID;

public record AdventureLorebookEntryDetails(
        UUID id,
        UUID adventureId,
        String name,
        String description,
        Instant creationDate,
        Instant lastUpdateDate) {
}
