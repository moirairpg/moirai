package me.moirai.discordbot.infrastructure.outbound.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, String> {

    List<FavoriteEntity> findAllByPlayerDiscordId(String playerDiscordId);
}
