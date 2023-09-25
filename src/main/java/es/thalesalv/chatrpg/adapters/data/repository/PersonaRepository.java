package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;

public interface PersonaRepository
        extends JpaRepository<PersonaEntity, String>, PaginationRepository<PersonaEntity, String> {
}
