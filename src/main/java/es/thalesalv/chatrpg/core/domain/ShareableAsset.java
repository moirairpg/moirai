package es.thalesalv.chatrpg.core.domain;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

public abstract class ShareableAsset extends Asset {

    private Permissions permissions;
    private Visibility visibility;

    protected ShareableAsset(String creatorDiscordId, OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate, Permissions permissions, Visibility visibility) {

        super(creatorDiscordId, creationDate, lastUpdateDate);
        this.permissions = permissions;
        this.visibility = visibility;
    }

    public boolean isPublic() {

        return this.visibility.equals(Visibility.PUBLIC);
    }

    public void makePublic() {

        this.visibility = Visibility.PUBLIC;
    }

    public void makePrivate() {

        this.visibility = Visibility.PRIVATE;
    }

    public Visibility getVisibility() {

        return visibility;
    }

    public boolean isOwner(String discordUserId) {

        return permissions.getOwnerDiscordId().equals(discordUserId);
    }

    public boolean canUserWrite(String discordUserId) {

        boolean isWriter = permissions.getUsersAllowedToWrite().contains(discordUserId);

        return isOwner(discordUserId) || isWriter;
    }

    public boolean canUserRead(String discordUserId) {

        boolean isReader = permissions.getUsersAllowedToRead().contains(discordUserId);

        return canUserWrite(discordUserId) || isReader;
    }

    public void addWriterUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .allowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void addReaderUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .allowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void removeWriterUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .disallowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public void removeReaderUser(String discordUserId) {

        Permissions newPermissions = this.permissions
                .disallowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = newPermissions;
    }

    public List<String> getWriterUsers() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToWrite());
    }

    public List<String> getReaderUsers() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToRead());
    }

    public String getOwnerDiscordId() {

        return this.permissions.getOwnerDiscordId();
    }
}
