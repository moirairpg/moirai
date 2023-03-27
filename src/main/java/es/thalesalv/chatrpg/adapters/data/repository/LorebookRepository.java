package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;

public interface LorebookRepository extends CrudRepository<LorebookEntity, String> {
}