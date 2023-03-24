package es.thalesalv.chatrpg.adapters.data.db.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;

public interface WorldRepository extends CrudRepository<WorldEntity, String> {

}
