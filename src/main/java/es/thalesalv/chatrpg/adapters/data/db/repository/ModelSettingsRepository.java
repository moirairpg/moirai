package es.thalesalv.chatrpg.adapters.data.db.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettingsEntity;

@Repository
public interface ModelSettingsRepository extends CrudRepository<ModelSettingsEntity, String> {

}
