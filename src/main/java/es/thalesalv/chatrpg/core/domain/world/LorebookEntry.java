package es.thalesalv.chatrpg.core.domain.world;

import java.time.OffsetDateTime;

import es.thalesalv.chatrpg.core.domain.Asset;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class LorebookEntry extends Asset {

    private String id;
    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;

    private LorebookEntry(Builder builder) {

        super(builder.creatorDiscordId, builder.creationDate, builder.lastUpdateDate);
        this.id = builder.id;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
    }

    public static Builder builder() {

        return new Builder();
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

        this.playerDiscordId = playerDiscordId;
    }

    public void unassignPlayer() {

        this.playerDiscordId = null;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private String id;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;
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

        public LorebookEntry build() {

            return new LorebookEntry(this);
        }
    }
}
