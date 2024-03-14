package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface ChannelConfigJpaRepository
        extends JpaRepository<ChannelConfigEntity, String>, PaginationRepository<ChannelConfigEntity, String> {

    static final String FIND_BY_ID = "SELECT cc FROM ChannelConfig cc WHERE cc.id = :id "
            + " AND (cc.ownerDiscordId = :requesterId "
            + "    OR concat(',', cc.usersAllowedToRead, ',') LIKE concat('%,', :requesterId, ',%') "
            + "    OR concat(',', cc.usersAllowedToWrite, ',') LIKE concat('%,', :requesterId, ',%'))";

    @Query(FIND_BY_ID)
    Optional<ChannelConfigEntity> findById(String id, String requesterId);
}
