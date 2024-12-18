package me.moirai.discordbot.infrastructure.outbound.persistence.mapper;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureLorebookEntryResult;
import me.moirai.discordbot.core.application.usecase.adventure.result.SearchAdventureLorebookEntriesResult;
import me.moirai.discordbot.core.domain.adventure.AdventureLorebookEntry;
import me.moirai.discordbot.infrastructure.outbound.persistence.adventure.AdventureLorebookEntryEntity;

@Component
public class AdventureLorebookPersistenceMapper {

    public AdventureLorebookEntryEntity mapToEntity(AdventureLorebookEntry entry) {

        return AdventureLorebookEntryEntity.builder()
                .id(entry.getId())
                .name(entry.getName())
                .description(entry.getDescription())
                .regex(entry.getRegex())
                .playerDiscordId(entry.getPlayerDiscordId())
                .isPlayerCharacter(entry.isPlayerCharacter())
                .adventureId(entry.getAdventureId())
                .creatorDiscordId(entry.getCreatorDiscordId())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .version(entry.getVersion())
                .build();
    }

    public AdventureLorebookEntry mapFromEntity(AdventureLorebookEntryEntity entry) {

        return AdventureLorebookEntry.builder()
                .id(entry.getId())
                .name(entry.getName())
                .description(entry.getDescription())
                .regex(entry.getRegex())
                .playerDiscordId(entry.getPlayerDiscordId())
                .isPlayerCharacter(entry.isPlayerCharacter())
                .adventureId(entry.getAdventureId())
                .creatorDiscordId(entry.getCreatorDiscordId())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .version(entry.getVersion())
                .build();
    }

    public GetAdventureLorebookEntryResult mapToResult(AdventureLorebookEntryEntity entry) {

        return GetAdventureLorebookEntryResult.builder()
                .id(entry.getId())
                .name(entry.getName())
                .description(entry.getDescription())
                .regex(entry.getRegex())
                .playerDiscordId(entry.getPlayerDiscordId())
                .isPlayerCharacter(entry.isPlayerCharacter())
                .creationDate(entry.getCreationDate())
                .lastUpdateDate(entry.getLastUpdateDate())
                .build();
    }

    public SearchAdventureLorebookEntriesResult mapToResult(Page<AdventureLorebookEntryEntity> pagedResult) {
        return SearchAdventureLorebookEntriesResult.builder()
                .results(pagedResult.getContent()
                        .stream()
                        .map(this::mapToResult)
                        .toList())
                .page(pagedResult.getNumber() + 1)
                .items(pagedResult.getNumberOfElements())
                .totalItems(pagedResult.getTotalElements())
                .totalPages(pagedResult.getTotalPages())
                .build();
    }
}
