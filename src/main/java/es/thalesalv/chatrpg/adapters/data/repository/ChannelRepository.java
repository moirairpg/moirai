package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<ChannelEntity, String> {

    List<ChannelEntity> findAllByChannelConfig(ChannelConfigEntity channelConfig);
}