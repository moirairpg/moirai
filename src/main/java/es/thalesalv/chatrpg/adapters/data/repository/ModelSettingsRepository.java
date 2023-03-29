package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;

public interface ModelSettingsRepository extends CrudRepository<ModelSettingsEntity, String> {
}
