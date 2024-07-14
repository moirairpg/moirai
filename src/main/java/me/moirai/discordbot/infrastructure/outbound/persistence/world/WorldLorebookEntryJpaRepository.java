package me.moirai.discordbot.infrastructure.outbound.persistence.world;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import me.moirai.discordbot.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldLorebookEntryJpaRepository
        extends JpaRepository<WorldLorebookEntryEntity, String>,
        PaginationRepository<WorldLorebookEntryEntity, String> {

    @Query(value = "SELECT entry.* FROM world_lorebook entry WHERE :name ~ entry.regex", nativeQuery = true)
    List<WorldLorebookEntryEntity> findAllByNameRegex(@Param("name") String name);
}