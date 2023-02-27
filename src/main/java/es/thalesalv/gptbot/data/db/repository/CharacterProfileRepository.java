package es.thalesalv.gptbot.data.db.repository;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.thalesalv.gptbot.data.db.entity.CharacterProfileEntity;

@Repository
public interface CharacterProfileRepository extends CrudRepository<CharacterProfileEntity, UUID> {

    /**
     * Retrieves a character from the database by providing the player's Discord ID
     * 
     * @param userId Player's Discord ID
     * @return Player's character profile
     */
    CharacterProfileEntity findByPlayerDiscordId(String userId);

    /**
     * Retrieves all characters that match the list of names provided
     * 
     * @param names List containing names to look up
     * @return Character profiles with those names
     */
    HashSet<CharacterProfileEntity> findByNameIn(@Param("names") HashSet<String> names);
}
