package es.thalesalv.gptbot.adapters.data.db.repository;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.gptbot.adapters.data.db.document.CharacterProfile;

@Repository
public interface CharacterProfileRepository extends MongoRepository<CharacterProfile, UUID> {

    /**
     * Retrieves a character from the database by providing the player's Discord ID
     * 
     * @param userId Player's Discord ID
     * @return Player's character profile
     */
    CharacterProfile findByPlayerDiscordId(String userId);

    /**
     * Retrieves all characters that match the list of names provided
     * 
     * @param names List containing names to look up
     * @return Character profiles with those names
     */
    HashSet<CharacterProfile> findByNameIn(HashSet<String> names);
}
