package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntryEntity;
import org.springframework.data.repository.CrudRepository;

public interface LorebookEntryRepository extends CrudRepository<LorebookEntryEntity, String> {

    List<LorebookEntryEntity> findAll();
}
