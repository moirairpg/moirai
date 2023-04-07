package es.thalesalv.chatrpg.adapters.data.repository;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LorebookEntryRepository extends JpaRepository<LorebookEntryEntity, String> {
}
