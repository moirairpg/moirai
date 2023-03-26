package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;

@Repository
public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {

    List<ChannelEntity> findAll();
    Optional<ChannelEntity> findByChannelId(String channelId);
}