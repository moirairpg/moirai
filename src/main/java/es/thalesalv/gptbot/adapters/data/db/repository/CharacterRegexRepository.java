package es.thalesalv.gptbot.adapters.data.db.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.gptbot.adapters.data.db.entity.CharacterRegexEntity;

@Repository
public interface CharacterRegexRepository extends CrudRepository<CharacterRegexEntity, UUID> {

    List<CharacterRegexEntity> findAll();
}
