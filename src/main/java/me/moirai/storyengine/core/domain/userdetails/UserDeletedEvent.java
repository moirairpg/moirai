package me.moirai.storyengine.core.domain.userdetails;

import java.util.UUID;

import me.moirai.storyengine.common.domain.DomainEvent;

public final class UserDeletedEvent implements DomainEvent {

    private final Long userId;
    private final UUID userPublicId;
    private final String username;

    UserDeletedEvent(Long userId, UUID userPublicId, String username) {

        this.userId = userId;
        this.userPublicId = userPublicId;
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public UUID getUserPublicId() {
        return userPublicId;
    }

    public String getUsername() {
        return username;
    }
}
