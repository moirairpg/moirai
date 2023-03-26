package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;

public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {

    List<ChannelEntity> findAll();
    Optional<ChannelEntity> findByChannelId(String channelId);
}