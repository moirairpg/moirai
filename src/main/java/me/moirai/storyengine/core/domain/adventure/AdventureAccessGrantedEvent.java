package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;
import me.moirai.storyengine.common.enums.PermissionLevel;

public final class AdventureAccessGrantedEvent implements DomainEvent {

    private final Long adventureId;
    private final UUID adventurePublicId;
    private final String adventureName;
    private final Long userId;
    private final PermissionLevel level;

    AdventureAccessGrantedEvent(
            Long adventureId,
            UUID adventurePublicId,
            String adventureName,
            Long userId,
            PermissionLevel level) {

        this.adventureId = adventureId;
        this.adventurePublicId = adventurePublicId;
        this.adventureName = adventureName;
        this.userId = userId;
        this.level = level;
    }

    public Long getAdventureId() {
        return adventureId;
    }

    public UUID getAdventurePublicId() {
        return adventurePublicId;
    }

    public String getAdventureName() {
        return adventureName;
    }

    public Long getUserId() {
        return userId;
    }

    public PermissionLevel getLevel() {
        return level;
    }
}
