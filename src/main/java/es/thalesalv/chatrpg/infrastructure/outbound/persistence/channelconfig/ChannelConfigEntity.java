package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import java.time.OffsetDateTime;
import java.util.List;

import org.hibernate.annotations.GenericGenerator;

import es.thalesalv.chatrpg.common.dbutil.NanoIdIdentifierGenerator;
import es.thalesalv.chatrpg.infrastructure.outbound.persistence.ShareableAssetEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "ChannelConfig")
@Table(name = "channel_config")
public class ChannelConfigEntity extends ShareableAssetEntity {

    @Id
    @GeneratedValue(generator = "nanoid-generator")
    @GenericGenerator(name = "nanoid-generator", type = NanoIdIdentifierGenerator.class)
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "world_id", nullable = false)
    private String worldId;

    @Column(name = "persona_id", nullable = false)
    private String personaId;

    @Column(name = "discord_channel_id", nullable = false)
    private String discordChannelId;

    @Embedded
    private ModelConfigurationEntity modelConfiguration;

    @Column(name = "moderation", nullable = false)
    private String moderation;

    private ChannelConfigEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite,
                builder.visibility);

        this.id = builder.id;
        this.name = builder.name;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.modelConfiguration = builder.modelConfiguration;
        this.moderation = builder.moderation;
        this.discordChannelId = builder.discordChannelId;
    }

    protected ChannelConfigEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private ModelConfigurationEntity modelConfiguration;
        private String moderation;
        protected String ownerDiscordId;
        protected List<String> usersAllowedToRead;
        protected List<String> usersAllowedToWrite;
        protected String visibility;
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

        public Builder discordChannelId(String discordChannelId) {

            this.discordChannelId = discordChannelId;
            return this;
        }

        public Builder modelConfiguration(ModelConfigurationEntity modelConfiguration) {

            this.modelConfiguration = modelConfiguration;
            return this;
        }

        public Builder moderation(String moderation) {

            this.moderation = moderation;
            return this;
        }

        public Builder visibility(String visibility) {

            this.visibility = visibility;
            return this;
        }

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

        public ChannelConfigEntity build() {

            return new ChannelConfigEntity(this);
        }
    }
}
