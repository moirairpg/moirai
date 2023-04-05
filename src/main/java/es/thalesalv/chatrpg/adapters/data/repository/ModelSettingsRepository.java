package es.thalesalv.chatrpg.adapters.data.repository;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelSettingsRepository extends JpaRepository<ModelSettingsEntity, String> {
}
