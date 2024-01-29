package es.thalesalv.chatrpg.core.domain.persona;

import static es.thalesalv.chatrpg.core.domain.Visibility.PRIVATE;
import static es.thalesalv.chatrpg.core.domain.Visibility.PUBLIC;

import java.util.Collections;
import java.util.List;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.Visibility;
import io.micrometer.common.util.StringUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Persona {

    private String id;
    private String name;
    private String personality;
    private Visibility visibility;
    private Permissions permissions;

    private Persona(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
        this.visibility = builder.visibility;
        this.permissions = builder.permissions;
    }

    public static Builder builder() {

        return new Builder();
    }

    public void makePublic() {

        this.visibility = PUBLIC;
    }

    public void makePrivate() {

        this.visibility = PRIVATE;
    }

    public boolean isPublic() {

        return this.visibility.equals(PUBLIC);
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updatePersonality(String personality) {

        this.personality = personality;
    }

    public List<String> getWriterUsers() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToWrite());
    }

    public List<String> getReaderUsers() {

        return Collections.unmodifiableList(this.permissions.getUsersAllowedToRead());
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

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String personality;
        private Visibility visibility;
        private Permissions permissions;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder personality(String personality) {

            this.personality = personality;
            return this;
        }

        public Builder visibility(Visibility visibility) {

            this.visibility = visibility;
            return this;
        }

        public Builder permissions(Permissions permissions) {

            this.permissions = permissions;
            return this;
        }

        public Persona build() {

            if (StringUtils.isBlank(name)) {
                throw new BusinessRuleViolationException("Persona name cannot be null or empty");
            }

            if (StringUtils.isBlank(personality)) {
                throw new BusinessRuleViolationException("Persona personality cannot be null or empty");
            }

            if (visibility == null) {
                throw new BusinessRuleViolationException("Visibility cannot be null");
            }

            if (permissions == null) {
                throw new BusinessRuleViolationException("Permissions cannot be null");
            }

            return new Persona(this);
        }
    }
}
