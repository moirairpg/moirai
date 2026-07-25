package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;
import me.moirai.storyengine.common.enums.InvitationStatus;

public final class AdventureInvitationAnsweredEvent implements DomainEvent {

    private final Long adventureId;
    private final UUID adventurePublicId;
    private final String adventureName;
    private final Long respondingUserId;
    private final InvitationStatus response;

    AdventureInvitationAnsweredEvent(
            Long adventureId,
            UUID adventurePublicId,
            String adventureName,
            Long respondingUserId,
            InvitationStatus response) {

        this.adventureId = adventureId;
        this.adventurePublicId = adventurePublicId;
        this.adventureName = adventureName;
        this.respondingUserId = respondingUserId;
        this.response = response;
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

    public Long getRespondingUserId() {
        return respondingUserId;
    }

    public InvitationStatus getResponse() {
        return response;
    }
}
