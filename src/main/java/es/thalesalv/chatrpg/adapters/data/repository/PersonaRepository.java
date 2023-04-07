package es.thalesalv.chatrpg.adapters.data.repository;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<PersonaEntity, String> {
}
