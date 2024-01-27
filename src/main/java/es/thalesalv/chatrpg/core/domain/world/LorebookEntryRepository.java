package es.thalesalv.chatrpg.core.domain.world;

import java.util.List;
import java.util.Optional;

public interface LorebookEntryRepository {

    Optional<LorebookEntry> findById(String lorebookEntryId);

    List<LorebookEntry> findByWorldId(String worldId);
}
