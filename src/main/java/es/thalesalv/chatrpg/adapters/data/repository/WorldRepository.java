package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;

public interface WorldRepository extends JpaRepository<WorldEntity, String> {
}