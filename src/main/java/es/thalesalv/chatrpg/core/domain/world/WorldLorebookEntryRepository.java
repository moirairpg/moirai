package es.thalesalv.chatrpg.core.domain.world;

import java.util.Optional;

import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntries;
import es.thalesalv.chatrpg.core.application.query.world.SearchWorldLorebookEntriesResult;

public interface WorldLorebookEntryRepository {

    WorldLorebookEntry save(WorldLorebookEntry lorebookEntry);

    Optional<WorldLorebookEntry> findById(String lorebookEntryId);

    SearchWorldLorebookEntriesResult searchWorldLorebookEntriesByWorldId(SearchWorldLorebookEntries query);

    void deleteById(String id);
}
