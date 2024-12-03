package me.moirai.discordbot.infrastructure.outbound.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, String> {

    void deleteByPlayerDiscordIdAndAssetIdAndAssetType(String playerDiscordId, String assetId, String assetType);
}
