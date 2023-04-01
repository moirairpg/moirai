package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;

public interface ModerationSettingsRepository extends CrudRepository<ModerationSettingsEntity, String> {

    List<ModerationSettingsEntity> findAll();
}
