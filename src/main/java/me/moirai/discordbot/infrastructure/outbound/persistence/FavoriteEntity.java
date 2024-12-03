package me.moirai.discordbot.infrastructure.outbound.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import me.moirai.discordbot.common.annotation.NanoId;

@Entity(name = "Favorite")
@Table(name = "favorite")
public class FavoriteEntity {

    @Id
    @NanoId
    private String id;

    @Column(name = "player_discord_id", nullable = false)
    private String playerDiscordId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Column(name = "asset_type", nullable = false)
    private String assetType;

    private FavoriteEntity(Builder builder) {
        this.playerDiscordId = builder.playerDiscordId;
        this.assetId = builder.assetId;
        this.assetType = builder.assetType;
    }

    protected FavoriteEntity() {
        super();
    }

    public static Builder builder() {

        return new Builder();
    }

    public String getId() {
        return id;
    }

    public String getPlayerDiscordId() {
        return playerDiscordId;
    }

    public String getAssetId() {
        return assetId;
    }

    public String getAssetType() {
        return assetType;
    }

    public static final class Builder {

        private String playerDiscordId;
        private String assetId;
        private String assetType;

        public Builder playerDiscordId(String playerDiscordId) {
            this.playerDiscordId = playerDiscordId;
            return this;
        }

        public Builder assetId(String assetId) {
            this.assetId = assetId;
            return this;
        }

        public Builder assetType(String assetType) {
            this.assetType = assetType;
            return this;
        }

        public FavoriteEntity build() {

            return new FavoriteEntity(this);
        }
    }
}
