package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.time.OffsetDateTime;

import org.apache.commons.lang3.StringUtils;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import es.thalesalv.chatrpg.core.domain.Permissions;
import es.thalesalv.chatrpg.core.domain.ShareableAsset;
import es.thalesalv.chatrpg.core.domain.Visibility;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// TODO implement missing VO methods (manage moderation and model config)
@Getter
public class ChannelConfig extends ShareableAsset {

    private String id;
    private String name;
    private String worldId;
    private String personaId;
    private ModelConfiguration modelConfiguration;
    private Moderation moderation;

    private ChannelConfig(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.permissions, builder.visibility);

        this.id = builder.id;
        this.name = builder.name;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.modelConfiguration = builder.modelConfiguration;
        this.moderation = builder.moderation;
    }

    public static Builder builder() {

        return new Builder();
    }

    public void updateName(String name) {

        this.name = name;
    }

    public void updatePersona(String personaId) {

        this.personaId = personaId;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String worldId;
        private String personaId;
        private ModelConfiguration modelConfiguration;
        private Moderation moderation;
        private Visibility visibility;
        private Permissions permissions;
        private String creatorDiscordId;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;

        public Builder id(String id) {

            this.id = id;
            return this;
        }

        public Builder name(String name) {

            this.name = name;
            return this;
        }

        public Builder worldId(String worldId) {

            this.worldId = worldId;
            return this;
        }

        public Builder personaId(String personaId) {

            this.personaId = personaId;
            return this;
        }

        public Builder modelConfiguration(ModelConfiguration modelConfiguration) {

            this.modelConfiguration = modelConfiguration;
            return this;
        }

        public Builder moderation(Moderation moderation) {

            this.moderation = moderation;
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

        public ChannelConfig build() {

            if (StringUtils.isBlank(name)) {
                throw new BusinessRuleViolationException("Channel config name cannot be null or empty");
            }

            if (modelConfiguration == null) {
                throw new BusinessRuleViolationException("Model configuration cannot be null");
            }

            if (moderation == null) {
                throw new BusinessRuleViolationException("Moderation cannot be null");
            }

            if (visibility == null) {
                throw new BusinessRuleViolationException("Visibility cannot be null");
            }

            if (permissions == null) {
                throw new BusinessRuleViolationException("Permissions cannot be null");
            }

            return new ChannelConfig(this);
        }
    }
}
