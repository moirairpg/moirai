package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorldRepository extends JpaRepository<WorldEntity, String> {

    List<WorldEntity> findByLorebook(LorebookEntity lorebook);
}