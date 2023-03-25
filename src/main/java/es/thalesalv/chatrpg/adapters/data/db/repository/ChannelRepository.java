package es.thalesalv.chatrpg.adapters.data.db.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;

public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {

    List<ChannelEntity> findAll();
    Optional<ChannelEntity> findByChannelId(String channelId);
    Optional<ChannelEntity> deleteByChannelId(String channelId);
}