package es.thalesalv.chatrpg.core.domain.model.lorebook;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LorebookEntry {

    private String id;
    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;

    public LorebookEntry(Builder builder) {

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

    public static class Builder {

        private String id;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;

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

        public LorebookEntry build() {

            return new LorebookEntry(this);
        }
    }
}
