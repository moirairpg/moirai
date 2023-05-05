package es.thalesalv.chatrpg.adapters.data.repository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelConfigRepository extends JpaRepository<ChannelConfigEntity, String> {
}
