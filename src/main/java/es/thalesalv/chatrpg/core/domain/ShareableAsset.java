package es.thalesalv.chatrpg.core.domain;

import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ShareableAsset {

    private Permissions permissions;
    private Visibility visibility;

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

    public void addWriterUser(String discordUserId) {

        Permissions permissions = this.permissions
                .allowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = permissions;
    }

    public void addReaderUser(String discordUserId) {

        Permissions permissions = this.permissions
                .allowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = permissions;
    }

    public void removeWriterUser(String discordUserId) {

        Permissions permissions = this.permissions
                .disallowUserToWrite(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = permissions;
    }

    public void removeReaderUser(String discordUserId) {

        Permissions permissions = this.permissions
                .disallowUserToRead(discordUserId, this.permissions.getOwnerDiscordId());

        this.permissions = permissions;
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
