package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;

public interface ModerationSettingsRepository extends CrudRepository<ModerationSettingsEntity, String> {
}
