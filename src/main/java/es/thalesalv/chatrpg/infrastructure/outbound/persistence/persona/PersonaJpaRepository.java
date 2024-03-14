package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface PersonaJpaRepository
        extends JpaRepository<PersonaEntity, String>, PaginationRepository<PersonaEntity, String> {

    static final String FIND_BY_ID = "SELECT p FROM Persona p WHERE p.id = :id "
            + " AND (p.ownerDiscordId = :requesterId "
            + "    OR concat(',', p.usersAllowedToRead, ',') LIKE concat('%,', :requesterId, ',%') "
            + "    OR concat(',', p.usersAllowedToWrite, ',') LIKE concat('%,', :requesterId, ',%'))";

    @Query(FIND_BY_ID)
    Optional<PersonaEntity> findById(String id, String requesterId);
}
