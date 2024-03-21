package es.thalesalv.chatrpg.infrastructure.outbound.persistence.world;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface WorldLorebookEntryJpaRepository
        extends JpaRepository<WorldLorebookEntryEntity, String>, PaginationRepository<WorldLorebookEntryEntity, String> {

}