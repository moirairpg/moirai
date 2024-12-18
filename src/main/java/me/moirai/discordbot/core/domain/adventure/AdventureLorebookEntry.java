package me.moirai.discordbot.core.domain.adventure;

import java.time.OffsetDateTime;

import me.moirai.discordbot.core.domain.Asset;

public class AdventureLorebookEntry extends Asset {

    private String id;
    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;
    private boolean isPlayerCharacter;
    private String adventureId;

    private AdventureLorebookEntry(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate, builder.version);
        this.id = builder.id;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
        this.isPlayerCharacter = builder.isPlayerCharacter;
        this.adventureId = builder.adventureId;
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

    public String getRegex() {
        return regex;
    }

    public String getDescription() {
        return description;
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

    public void updateName(String name) {

        this.name = name;
    }

    public void updateDescription(String description) {

        this.description = description;
    }

    public void updateRegex(String regex) {

        this.regex = regex;
    }

    public void assignPlayer(String playerDiscordId) {

        this.isPlayerCharacter = true;
        this.playerDiscordId = playerDiscordId;
    }

    public void unassignPlayer() {

        this.isPlayerCharacter = false;
        this.playerDiscordId = null;
    }

    public static final class Builder {

        private String id;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;
        private String creatorDiscordId;
        private boolean isPlayerCharacter;
        private String adventureId;
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

        public AdventureLorebookEntry build() {

            return new AdventureLorebookEntry(this);
        }
    }
}
