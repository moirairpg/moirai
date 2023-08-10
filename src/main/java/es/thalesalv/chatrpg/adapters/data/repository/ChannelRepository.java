package es.thalesalv.chatrpg.adapters.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;

public interface ChannelRepository
        extends JpaRepository<ChannelEntity, String>, PaginationRepository<ChannelEntity, String> {

    List<ChannelEntity> findAllByChannelConfig(ChannelConfigEntity channelConfig);
}