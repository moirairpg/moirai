package me.moirai.storyengine.core.domain.adventure;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class UserInvitedToAdventureEvent implements DomainEvent {

    private final UUID invitationPublicId;

    UserInvitedToAdventureEvent(UUID invitationPublicId) {
        this.invitationPublicId = invitationPublicId;
    }

    public UUID getInvitationPublicId() {
        return invitationPublicId;
    }
}
