package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface ChannelConfigJpaRepository
        extends JpaRepository<ChannelConfigEntity, String>, PaginationRepository<ChannelConfigEntity, String> {

    Optional<ChannelConfigEntity> findByDiscordChannelId(String channelId);

    @Query("SELECT cc.gameMode FROM ChannelConfig cc WHERE cc.discordChannelId = :channelId")
    String getGameModeByDiscordChannelId(String channelId);
}
