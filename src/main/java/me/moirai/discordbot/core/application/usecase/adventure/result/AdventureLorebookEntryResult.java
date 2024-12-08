package me.moirai.discordbot.core.application.usecase.adventure.result;

public final class AdventureLorebookEntryResult {

    private String name;
    private String regex;
    private String description;
    private String playerDiscordId;

    private AdventureLorebookEntryResult(Builder builder) {

        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
    }

    public static Builder builder() {
        return new Builder();
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

    public static final class Builder {

        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;

        private Builder() {
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder regex(String val) {
            regex = val;
            return this;
        }

        public Builder description(String val) {
            description = val;
            return this;
        }

        public Builder playerDiscordId(String val) {
            playerDiscordId = val;
            return this;
        }

        public AdventureLorebookEntryResult build() {
            return new AdventureLorebookEntryResult(this);
        }
    }
}