package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ModerationSettingsEntity;

public interface ModerationSettingsRepository extends JpaRepository<ModerationSettingsEntity, String>,
        PaginationRepository<ModerationSettingsEntity, String> {
}
