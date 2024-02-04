package es.thalesalv.chatrpg.core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public final class Permissions {

    private final String ownerDiscordId;
    private final List<String> usersAllowedToRead;
    private final List<String> usersAllowedToWrite;

    private Permissions(Builder builder) {

        this.ownerDiscordId = builder.ownerDiscordId;

        List<String> usersAllowedToRead = builder.usersAllowedToRead == null ? Collections.emptyList()
                : builder.usersAllowedToRead;

        this.usersAllowedToRead = Collections.unmodifiableList(usersAllowedToRead);

        List<String> usersAllowedToWrite = builder.usersAllowedToRead == null ? Collections.emptyList()
                : builder.usersAllowedToWrite;

        this.usersAllowedToWrite = Collections.unmodifiableList(usersAllowedToWrite);
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(Permissions permissions) {

        return builder().ownerDiscordId(permissions.getOwnerDiscordId())
                .usersAllowedToRead(permissions.getUsersAllowedToRead())
                .usersAllowedToWrite(permissions.getUsersAllowedToWrite());
    }

    public Permissions updateOwner(String newOwnerDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        return cloneFrom(this).ownerDiscordId(newOwnerDiscordId).build();
    }

    public Permissions allowUserToWrite(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToWrite = new ArrayList<>(this.usersAllowedToWrite);
        usersAllowedToWrite.add(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions disallowUserToWrite(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToWrite = new ArrayList<>(this.usersAllowedToWrite);
        usersAllowedToWrite.remove(userDiscordId);

        return cloneFrom(this).usersAllowedToWrite(usersAllowedToWrite).build();
    }

    public Permissions allowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToRead = new ArrayList<>(this.usersAllowedToRead);
        usersAllowedToRead.add(userDiscordId);

        return cloneFrom(this).usersAllowedToRead(usersAllowedToRead).build();
    }

    public Permissions disallowUserToRead(String userDiscordId, String currentOwnerDiscordId) {

        validateOwnership(currentOwnerDiscordId);
        List<String> usersAllowedToRead = new ArrayList<>(this.usersAllowedToRead);
        usersAllowedToRead.remove(userDiscordId);

        return cloneFrom(this).usersAllowedToRead(usersAllowedToRead).build();
    }

    public void validateOwnership(String currentOwnerDiscordId) {

        if (!this.ownerDiscordId.equals(currentOwnerDiscordId)) {
            throw new BusinessRuleViolationException("Operation not permitted: user does not own this asset");
        }
    }

    public boolean isOwner(String discordUserId) {

        return this.ownerDiscordId.equals(discordUserId);
    }

    public boolean isAllowedToWrite(String discordUserId) {

        return this.usersAllowedToWrite.contains(discordUserId) || isOwner(discordUserId);
    }

    public boolean isAllowedToRead(String discordUserId) {

        return this.usersAllowedToRead.contains(discordUserId) || isAllowedToWrite(discordUserId);
    }

    public boolean areAllowedToWrite(List<String> discordUserIds) {

        boolean isOwnerFound = discordUserIds.stream().anyMatch(this::isOwner);

        return !Collections.disjoint(this.usersAllowedToWrite, discordUserIds) || isOwnerFound;
    }

    public boolean areAllowedToRead(List<String> discordUserIds) {

        return !Collections.disjoint(this.usersAllowedToRead, discordUserIds) || areAllowedToWrite(discordUserIds);
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;

        public Builder ownerDiscordId(String ownerDiscordId) {

            this.ownerDiscordId = ownerDiscordId;
            return this;
        }

        public Builder usersAllowedToRead(List<String> usersAllowedToRead) {

            this.usersAllowedToRead = usersAllowedToRead;
            return this;
        }

        public Builder usersAllowedToWrite(List<String> usersAllowedToWrite) {

            this.usersAllowedToWrite = usersAllowedToWrite;
            return this;
        }

        public Permissions build() {

            return new Permissions(this);
        }
    }
}
