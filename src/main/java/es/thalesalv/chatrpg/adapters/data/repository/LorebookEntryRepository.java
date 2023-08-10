package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;

public interface LorebookEntryRepository
        extends JpaRepository<LorebookEntryEntity, String>, PaginationRepository<LorebookEntryEntity, String> {
}
