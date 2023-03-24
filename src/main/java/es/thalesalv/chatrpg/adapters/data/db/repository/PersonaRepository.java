package es.thalesalv.chatrpg.adapters.data.db.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.db.entity.PersonaEntity;

public interface PersonaRepository extends CrudRepository<PersonaEntity, String> {

}
