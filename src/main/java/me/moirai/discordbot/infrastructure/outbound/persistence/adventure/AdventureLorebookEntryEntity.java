package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;
import me.moirai.discordbot.infrastructure.outbound.persistence.AssetEntity;

@Entity(name = "AdventureLorebookEntry")
@Table(name = "adventure_lorebook")
public class AdventureLorebookEntryEntity extends AssetEntity {

    @Id
    @NanoId
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "player_discord_id")
    private String playerDiscordId;

    @Column(name = "is_player_character", nullable = false)
    private boolean isPlayerCharacter;

    @Column(name = "adventure_id", nullable = false)
    private String adventureId;

    public AdventureLorebookEntryEntity(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate, builder.version);

        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.regex = builder.regex;
        this.isPlayerCharacter = builder.isPlayerCharacter;
        this.adventureId = builder.adventureId;
        this.playerDiscordId = builder.playerDiscordId;
    }

    protected AdventureLorebookEntryEntity() {
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

    public String getRegex() {
        return regex;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public boolean isPlayerCharacter() {
        return isPlayerCharacter;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String description;
        private String regex;
        private String playerDiscordId;
        private boolean isPlayerCharacter;
        private String adventureId;
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

        public Builder description(String description) {

            this.description = description;
            return this;
        }

        public Builder regex(String regex) {

            this.regex = regex;
            return this;
        }

        public Builder playerDiscordId(String playerDiscordId) {

            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public Builder isPlayerCharacter(boolean isPlayerCharacter) {

            this.isPlayerCharacter = isPlayerCharacter;
            return this;
        }

        public Builder adventureId(String adventureId) {

            this.adventureId = adventureId;
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

        public AdventureLorebookEntryEntity build() {

            return new AdventureLorebookEntryEntity(this);
        }
    }
}
