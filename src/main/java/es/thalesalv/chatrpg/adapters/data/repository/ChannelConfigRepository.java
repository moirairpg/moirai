package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;

@Repository
public interface ChannelConfigRepository extends CrudRepository<ChannelConfigEntity, String> {

}
