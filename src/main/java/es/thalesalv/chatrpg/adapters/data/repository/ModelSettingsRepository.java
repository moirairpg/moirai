package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ModelSettingsEntity;

public interface ModelSettingsRepository
        extends JpaRepository<ModelSettingsEntity, String>, PaginationRepository<ModelSettingsEntity, String> {
}
