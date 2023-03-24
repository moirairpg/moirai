package es.thalesalv.chatrpg.adapters.data.db.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.db.entity.ModerationSettingsEntity;

public interface ModerationSettingsRepository extends CrudRepository<ModerationSettingsEntity, String> {

}
