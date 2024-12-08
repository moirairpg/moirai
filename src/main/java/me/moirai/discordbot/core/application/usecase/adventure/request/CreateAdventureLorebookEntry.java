package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.CreateAdventureLorebookEntryResult;

public final class CreateAdventureLorebookEntry extends UseCase<CreateAdventureLorebookEntryResult> {

    private final String adventureId;
    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;
    private final String requesterDiscordId;

    private CreateAdventureLorebookEntry(Builder builder) {

        this.adventureId = builder.adventureId;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAdventureId() {
        return adventureId;
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

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String adventureId;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder adventureId(String adventureId) {
            this.adventureId = adventureId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder playerDiscordId(String playerDiscordId) {
            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public CreateAdventureLorebookEntry build() {
            return new CreateAdventureLorebookEntry(this);
        }
    }
}