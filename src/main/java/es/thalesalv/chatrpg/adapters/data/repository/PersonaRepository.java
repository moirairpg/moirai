package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.PersonaEntity;

public interface PersonaRepository extends CrudRepository<PersonaEntity, String> {

    List<PersonaEntity> findAll();
}
