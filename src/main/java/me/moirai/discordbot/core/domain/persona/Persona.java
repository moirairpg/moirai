package me.moirai.discordbot.core.domain.persona;

import java.time.OffsetDateTime;

import io.micrometer.common.util.StringUtils;
import me.moirai.discordbot.common.exception.BusinessRuleViolationException;
import me.moirai.discordbot.core.domain.Permissions;
import me.moirai.discordbot.core.domain.ShareableAsset;
import me.moirai.discordbot.core.domain.Visibility;

public class Persona extends ShareableAsset {

    private String id;
    private String name;
    private String personality;

    private Persona(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.permissions, builder.visibility, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.personality = builder.personality;
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updatePersonality(String personality) {

        this.personality = personality;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String personality;
        private Visibility visibility;
        private Permissions permissions;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;

        private Builder() {
        }

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

        public Builder creatorDiscordId(String creatorDiscordId) {

            this.creatorDiscordId = creatorDiscordId;
            return this;
        }

        public Builder creationDate(OffsetDateTime creationDate) {

            this.creationDate = creationDate;
            return this;
        }

        public Builder lastUpdateDate(OffsetDateTime lastUpdateDate) {

            this.lastUpdateDate = lastUpdateDate;
            return this;
        }

        public Builder version(int version) {

            this.version = version;
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
