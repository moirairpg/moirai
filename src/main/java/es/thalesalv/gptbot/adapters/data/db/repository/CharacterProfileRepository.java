package es.thalesalv.gptbot.adapters.data.db.repository;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.gptbot.adapters.data.db.entity.CharacterProfileEntity;

@Repository
public interface CharacterProfileRepository extends CrudRepository<CharacterProfileEntity, UUID> {

    /**
     * Retrieves a character from the database by providing the player's Discord ID
     * 
     * @param userId Player's Discord ID
     * @return Player's character profile
     */
    Optional<CharacterProfileEntity> findByPlayerDiscordId(String userId);

    /**
     * Retrieves all characters that match the list of names provided
     * 
     * @param names List containing names to look up
     * @return Character profiles with those names
     */
    HashSet<CharacterProfileEntity> findByNameIn(HashSet<String> names);
}
