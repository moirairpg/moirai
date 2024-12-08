package me.moirai.discordbot.infrastructure.outbound.persistence.adventure;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface AdventureLorebookEntryJpaRepository
        extends JpaRepository<AdventureLorebookEntryEntity, String>,
        PaginationRepository<AdventureLorebookEntryEntity, String> {

    @Query(value = "SELECT entry.* FROM adventure_lorebook entry WHERE :valueToMatch ~ entry.regex AND entry.adventure_id = :adventureId", nativeQuery = true)
    List<AdventureLorebookEntryEntity> findAllByNameRegex(String valueToMatch, String adventureId);

    @Query(value = "SELECT entry.* FROM adventure_lorebook entry WHERE entry.player_discord_id = :playerDiscordId AND entry.adventure_id = :adventureId", nativeQuery = true)
    Optional<AdventureLorebookEntryEntity> findByPlayerDiscordId(String playerDiscordId, String adventureId);

    List<AdventureLorebookEntryEntity> findAllByAdventureId(String adventureId);

    void deleteAllByAdventureId(String adventureId);
}