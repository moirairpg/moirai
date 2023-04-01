package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.LorebookEntity;
import org.springframework.data.repository.CrudRepository;

public interface LorebookRepository extends CrudRepository<LorebookEntity, String> {

    List<LorebookEntity> findAll();
}