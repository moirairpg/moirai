package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface AdventureJpaRepository
        extends JpaRepository<AdventureEntity, String>, PaginationRepository<AdventureEntity, String> {

    Optional<AdventureEntity> findByDiscordChannelId(String channelId);

    @Query("SELECT cc.gameMode FROM Adventure cc WHERE cc.discordChannelId = :channelId")
    String getGameModeByDiscordChannelId(String channelId);
}
