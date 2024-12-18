package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class RemoveFavoriteAdventure extends UseCase<Void> {

    private final String assetId;
    private final String playerDiscordId;

    private RemoveFavoriteAdventure(Builder builder) {
        this.assetId = builder.assetId;
        this.playerDiscordId = builder.playerDiscordId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAssetId() {
        return assetId;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public static final class Builder {

        private String assetId;
        private String playerDiscordId;

        public Builder assetId(String assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder playerDiscordId(String playerDiscordId) {
            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public RemoveFavoriteAdventure build() {
            return new RemoveFavoriteAdventure(this);
        }
    }
}
