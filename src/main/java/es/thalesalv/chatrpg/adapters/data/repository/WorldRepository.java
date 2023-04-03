package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import org.springframework.data.repository.CrudRepository;

public interface WorldRepository extends CrudRepository<WorldEntity, String> {

    List<WorldEntity> findAll();
    List<WorldEntity> findByLorebook(LorebookEntity lorebook);
}