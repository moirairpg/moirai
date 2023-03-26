package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;

public interface ChannelConfigRepository extends CrudRepository<ChannelConfigEntity, String> {

}