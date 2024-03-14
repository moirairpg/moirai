package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldJpaRepository
        extends JpaRepository<WorldEntity, String>, PaginationRepository<WorldEntity, String> {

    static final String FIND_BY_ID = "SELECT w FROM World w WHERE w.id = :id "
            + " AND (w.ownerDiscordId = :requesterId "
            + "    OR concat(',', w.usersAllowedToRead, ',') LIKE concat('%,', :requesterId, ',%') "
            + "    OR concat(',', w.usersAllowedToWrite, ',') LIKE concat('%,', :requesterId, ',%'))";

    @Query(FIND_BY_ID)
    Optional<WorldEntity> findById(String id, String requesterId);
}