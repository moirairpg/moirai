package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;

public interface LorebookEntryRepository extends CrudRepository<LorebookEntryEntity, String> {

    /**
     * Retrieves a character from the database by providing the player's Discord ID
     *
     * @param userId Player's Discord ID
     * @return Player's character profile
     */
    Optional<LorebookEntryEntity> findByPlayerDiscordId(String userId);

    /**
     * Retrieves all characters that match the list of names provided
     *
     * @param names List containing names to look up
     * @return Character profiles with those names
     */
    Set<LorebookEntryEntity> findByNameIn(Set<String> names);
}
