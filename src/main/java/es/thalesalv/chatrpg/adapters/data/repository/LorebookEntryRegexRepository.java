package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryRegexEntity;

public interface LorebookEntryRegexRepository extends CrudRepository<LorebookEntryRegexEntity, String> {

    List<LorebookEntryRegexEntity> findAll();
    void deleteByLorebookEntry(LorebookEntryEntity lorebookEntry);
    Optional<LorebookEntryRegexEntity> findByLorebookEntry(LorebookEntryEntity lorebookEntry);
}
