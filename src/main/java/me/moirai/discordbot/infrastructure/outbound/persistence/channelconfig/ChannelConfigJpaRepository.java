package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface ChannelConfigJpaRepository
        extends JpaRepository<ChannelConfigEntity, String>, PaginationRepository<ChannelConfigEntity, String> {

    Optional<ChannelConfigEntity> findByDiscordChannelId(String channelId);
}
