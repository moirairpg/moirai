package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureLorebookEntryResult;

public final class GetAdventureLorebookEntryById extends UseCase<GetAdventureLorebookEntryResult> {

    private final String entryId;
    private final String adventureId;
    private final String requesterDiscordId;

    private GetAdventureLorebookEntryById(Builder builder) {

        this.entryId = builder.entryId;
        this.adventureId = builder.adventureId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEntryId() {
        return entryId;
    }

    public String getAdventureId() {
        return adventureId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String entryId;
        private String adventureId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder entryId(String entryId) {
            this.entryId = entryId;
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

        public GetAdventureLorebookEntryById build() {
            return new GetAdventureLorebookEntryById(this);
        }
    }
}