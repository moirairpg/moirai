package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.infrastructure.outbound.persistence.ShareableAssetEntity;

@Entity(name = "Adventure")
@Table(name = "adventure")
public class AdventureEntity extends ShareableAssetEntity {

    @Id
    @NanoId
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "world_id", nullable = false)
    private String worldId;

    @Column(name = "persona_id", nullable = false)
    private String personaId;

    @Column(name = "discord_channel_id", nullable = false)
    private String discordChannelId;

    @Column(name = "game_mode", nullable = false)
    private String gameMode;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "adventure_start", nullable = false)
    private String adventureStart;

    @Column(name = "is_multiplayer", nullable = false)
    private boolean isMultiplayer;

    @Embedded
    private ContextAttributesEntity contextAttributes;

    @Embedded
    private ModelConfigurationEntity modelConfiguration;

    @Column(name = "moderation", nullable = false)
    private String moderation;

    private AdventureEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite,
                builder.visibility, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
        this.worldId = builder.worldId;
        this.personaId = builder.personaId;
        this.gameMode = builder.gameMode;
        this.modelConfiguration = builder.modelConfiguration;
        this.moderation = builder.moderation;
        this.discordChannelId = builder.discordChannelId;
        this.isMultiplayer = builder.isMultiplayer;
        this.contextAttributes = builder.contextAttributes;
    }

    protected AdventureEntity() {
        super();
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

    public String getWorldId() {
        return worldId;
    }

    public String getPersonaId() {
        return personaId;
    }

    public String getDiscordChannelId() {
        return discordChannelId;
    }

    public ModelConfigurationEntity getModelConfiguration() {
        return modelConfiguration;
    }

    public String getModeration() {
        return moderation;
    }

    public boolean isMultiplayer() {
        return isMultiplayer;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public ContextAttributesEntity getContextAttributes() {
        return contextAttributes;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String description;
        private String adventureStart;
        private String worldId;
        private String personaId;
        private String discordChannelId;
        private String gameMode;
        private String visibility;
        private String moderation;
        private String ownerDiscordId;
        private String creatorDiscordId;
        private boolean isMultiplayer;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private OffsetDateTime creationDate;
        private OffsetDateTime lastUpdateDate;
        private int version;
        private ModelConfigurationEntity modelConfiguration;
        private ContextAttributesEntity contextAttributes;

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

        public Builder description(String description) {

            this.description = description;
            return this;
        }

        public Builder adventureStart(String adventureStart) {

            this.adventureStart = adventureStart;
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

        public Builder gameMode(String gameMode) {

            this.gameMode = gameMode;
            return this;
        }

        public Builder contextAttributes(ContextAttributesEntity contextAttributes) {

            this.contextAttributes = contextAttributes;
            return this;
        }

        public Builder isMultiplayer(boolean isMultiplayer) {

            this.isMultiplayer = isMultiplayer;
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

        public AdventureEntity build() {

            return new AdventureEntity(this);
        }
    }
}
