package es.thalesalv.chatrpg.adapters.data.db.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookEntry;
import es.thalesalv.chatrpg.adapters.data.db.entity.LorebookRegex;

@Repository
public interface LorebookRegexRepository extends CrudRepository<LorebookRegex, UUID> {

    List<LorebookRegex> findAll();
    void deleteByLorebookEntry(LorebookEntry lorebookEntry);
    Optional<LorebookRegex> findByLorebookEntry(LorebookEntry lorebookEntry);
}
