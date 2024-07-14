package me.moirai.discordbot.core.application.usecase.world.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.world.result.GetWorldLorebookEntryResult;

public final class GetWorldLorebookEntryById extends UseCase<GetWorldLorebookEntryResult> {

    private final String entryId;
    private final String worldId;
    private final String requesterDiscordId;

    private GetWorldLorebookEntryById(Builder builder) {

        this.entryId = builder.entryId;
        this.worldId = builder.worldId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getEntryId() {
        return entryId;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {

        private String entryId;
        private String worldId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder entryId(String entryId) {
            this.entryId = entryId;
            return this;
        }

        public Builder worldId(String worldId) {
            this.worldId = worldId;
            return this;
        }

        public Builder requesterDiscordId(String requesterDiscordId) {
            this.requesterDiscordId = requesterDiscordId;
            return this;
        }

        public GetWorldLorebookEntryById build() {
            return new GetWorldLorebookEntryById(this);
        }
    }
}