package es.thalesalv.chatrpg.adapters.data.db.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.db.entity.Channel;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, String> {

    List<Channel> findAll();
}