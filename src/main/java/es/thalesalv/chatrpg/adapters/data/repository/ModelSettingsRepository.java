package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;

public interface ModelSettingsRepository extends CrudRepository<ModelSettingsEntity, String> {

    List<ModelSettingsEntity> findAll();
}
