package es.thalesalv.chatrpg.adapters.data.repository;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModerationSettingsRepository extends JpaRepository<ModerationSettingsEntity, String> {
}
