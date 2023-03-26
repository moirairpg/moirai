package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;

@Repository
public interface ModerationSettingsRepository extends CrudRepository<ModerationSettingsEntity, String> {

}
