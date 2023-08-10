package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;

public interface ChannelConfigRepository
        extends JpaRepository<ChannelConfigEntity, String>, JpaSpecificationExecutor<ChannelConfigEntity> {
}
