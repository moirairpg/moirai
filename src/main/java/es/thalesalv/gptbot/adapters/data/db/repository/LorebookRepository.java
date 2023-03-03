package es.thalesalv.gptbot.adapters.data.db.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.gptbot.adapters.data.db.entity.LorebookEntry;

@Repository
public interface LorebookRepository extends CrudRepository<LorebookEntry, UUID> {

    /**
     * Retrieves a character from the database by providing the player's Discord ID
     * 
     * @param userId Player's Discord ID
     * @return Player's character profile
     */
    Optional<LorebookEntry> findByPlayerDiscordId(String userId);

    /**
     * Retrieves all characters that match the list of names provided
     * 
     * @param names List containing names to look up
     * @return Character profiles with those names
     */
    HashSet<LorebookEntry> findByNameIn(HashSet<String> names);
}
