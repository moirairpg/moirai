package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.UpdateAdventureLorebookEntryResult;
import reactor.core.publisher.Mono;

public final class UpdateAdventureLorebookEntry extends UseCase<Mono<UpdateAdventureLorebookEntryResult>> {

    private final String id;
    private final String adventureId;
    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;
    private final boolean isPlayerCharacter;
    private final String requesterDiscordId;

    private UpdateAdventureLorebookEntry(Builder builder) {

        this.id = builder.id;
        this.adventureId = builder.adventureId;
        this.name = builder.name;
        this.regex = builder.regex;
        this.description = builder.description;
        this.playerDiscordId = builder.playerDiscordId;
        this.isPlayerCharacter = builder.isPlayerCharacter;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getId() {
        return id;
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

    public boolean isPlayerCharacter() {
        return isPlayerCharacter;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String id;
        private String adventureId;
        private String name;
        private String regex;
        private String description;
        private String playerDiscordId;
        private boolean isPlayerCharacter;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder id(String id) {
            this.id = id;
            return this;
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

        public Builder isPlayerCharacter(boolean isPlayerCharacter) {
            this.isPlayerCharacter = isPlayerCharacter;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public UpdateAdventureLorebookEntry build() {
            return new UpdateAdventureLorebookEntry(this);
        }
    }
}