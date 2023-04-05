package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.Optional;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LorebookEntryRegexRepository extends JpaRepository<LorebookEntryRegexEntity, String> {


    void deleteByLorebookEntry(LorebookEntryEntity lorebookEntry);

    Optional<LorebookEntryRegexEntity> findByLorebookEntry(LorebookEntryEntity lorebookEntry);
}
