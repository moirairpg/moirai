package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;

public final class DeleteWorldLorebookEntry extends UseCase<Void> {

    private final String lorebookEntryId;
    private final String worldId;
    private final String requesterDiscordId;

    private DeleteWorldLorebookEntry(Builder builder) {
        this.lorebookEntryId = builder.lorebookEntryId;
        this.worldId = builder.worldId;
        this.requesterDiscordId = builder.requesterDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getLorebookEntryId() {
        return lorebookEntryId;
    }

    public String getWorldId() {
        return worldId;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }

    public static final class Builder {
        private String lorebookEntryId;
        private String worldId;
        private String requesterDiscordId;

        private Builder() {
        }

        public Builder lorebookEntryId(String lorebookEntryId) {
            this.lorebookEntryId = lorebookEntryId;
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

        public DeleteWorldLorebookEntry build() {
            return new DeleteWorldLorebookEntry(this);
        }
    }
}