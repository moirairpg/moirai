package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.infrastructure.outbound.persistence.PaginationRepository;

public interface PersonaJpaRepository
        extends JpaRepository<PersonaEntity, String>, PaginationRepository<PersonaEntity, String> {

}
