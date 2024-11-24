package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.infrastructure.outbound.persistence.ShareableAssetEntity;

@Entity(name = "World")
@Table(name = "world")
public class WorldEntity extends ShareableAssetEntity {

    @Id
    @NanoId
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "adventure_start", nullable = false)
    private String adventureStart;

    public WorldEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate,
                builder.lastUpdateDate, builder.ownerDiscordId, builder.usersAllowedToRead, builder.usersAllowedToWrite,
                builder.visibility, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.adventureStart = builder.adventureStart;
    }

    protected WorldEntity() {
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

    public String getDescription() {
        return description;
    }

    public String getAdventureStart() {
        return adventureStart;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String adventureStart;
        private String description;
        private String ownerDiscordId;
        private List<String> usersAllowedToRead;
        private List<String> usersAllowedToWrite;
        private String visibility;
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

        public Builder adventureStart(String adventureStart) {

            this.adventureStart = adventureStart;
            return this;
        }

        public Builder description(String description) {

            this.description = description;
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

        public Builder version(int version) {

            this.version = version;
            return this;
        }

        public WorldEntity build() {

            return new WorldEntity(this);
        }
    }
}
