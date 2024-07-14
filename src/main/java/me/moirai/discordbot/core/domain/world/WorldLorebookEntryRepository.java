package me.moirai.discordbot.core.domain.world;

import java.util.List;
import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.world.request.SearchWorldLorebookEntries;
import me.moirai.discordbot.core.application.usecase.world.result.SearchWorldLorebookEntriesResult;

public interface WorldLorebookEntryRepository {

    WorldLorebookEntry save(WorldLorebookEntry lorebookEntry);

    Optional<WorldLorebookEntry> findById(String lorebookEntryId);

    SearchWorldLorebookEntriesResult searchWorldLorebookEntriesByWorldId(SearchWorldLorebookEntries query);

    void deleteById(String id);

    List<WorldLorebookEntry> findAllEntriesByRegex(String valueToSearch);
}
