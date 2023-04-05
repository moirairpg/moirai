package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import org.springframework.data.repository.CrudRepository;

public interface ChannelRepository extends CrudRepository<ChannelEntity, String> {

    List<ChannelEntity> findAll();

    List<ChannelEntity> findAllByChannelConfig(ChannelConfigEntity channelConfig);
}