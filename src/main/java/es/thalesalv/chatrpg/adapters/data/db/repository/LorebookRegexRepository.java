package es.thalesalv.chatrpg.adapters.data.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntryEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegexEntity;

@Repository
public interface LorebookRegexRepository extends CrudRepository<LorebookRegexEntity, String> {

    List<LorebookRegexEntity> findAll();
    void deleteByLorebookEntry(LorebookEntryEntity lorebookEntry);
    Optional<LorebookRegexEntity> findByLorebookEntry(LorebookEntryEntity lorebookEntry);
}
