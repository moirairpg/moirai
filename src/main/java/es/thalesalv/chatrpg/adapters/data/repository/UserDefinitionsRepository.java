package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.UserDefinitionsEntity;

public interface UserDefinitionsRepository extends JpaRepository<UserDefinitionsEntity, String> {

}