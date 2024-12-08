package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class DeleteAdventureLorebookEntry extends UseCase<Void> {

    private final String lorebookEntryId;
    private final String adventureId;
    private final String requesterDiscordId;

    private DeleteAdventureLorebookEntry(Builder builder) {

        this.lorebookEntryId = builder.lorebookEntryId;
        this.adventureId = builder.adventureId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLorebookEntryId() {
        return lorebookEntryId;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String lorebookEntryId;
        private String adventureId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder lorebookEntryId(String lorebookEntryId) {
            this.lorebookEntryId = lorebookEntryId;
            return this;
        }

        public Builder adventureId(String adventureId) {
            this.adventureId = adventureId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public DeleteAdventureLorebookEntry build() {
            return new DeleteAdventureLorebookEntry(this);
        }
    }
}