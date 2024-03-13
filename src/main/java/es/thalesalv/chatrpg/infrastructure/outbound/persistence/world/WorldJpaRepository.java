package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldJpaRepository
        extends JpaRepository<WorldEntity, String>, PaginationRepository<WorldEntity, String> {
}