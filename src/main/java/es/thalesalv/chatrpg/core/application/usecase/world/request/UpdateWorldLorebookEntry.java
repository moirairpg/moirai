package es.thalesalv.chatrpg.core.application.usecase.world.request;

import es.thalesalv.chatrpg.common.usecases.UseCase;
import es.thalesalv.chatrpg.core.application.usecase.world.result.UpdateWorldLorebookEntryResult;

public final class UpdateWorldLorebookEntry extends UseCase<UpdateWorldLorebookEntryResult> {

    private final String id;
    private final String worldId;
    private final String name;
    private final String regex;
    private final String description;
    private final String playerDiscordId;
    private final boolean isPlayerCharacter;
    private final String requesterDiscordId;

    private UpdateWorldLorebookEntry(Builder builder) {
        this.id = builder.id;
        this.worldId = builder.worldId;
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

    public String getWorldId() {
        return worldId;
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
        private String worldId;
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

        public Builder worldId(String worldId) {
            this.worldId = worldId;
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

        public UpdateWorldLorebookEntry build() {
            return new UpdateWorldLorebookEntry(this);
        }
    }
}