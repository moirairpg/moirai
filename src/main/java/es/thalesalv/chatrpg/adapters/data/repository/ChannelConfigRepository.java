package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;

public interface ChannelConfigRepository
        extends JpaRepository<ChannelConfigEntity, String>, PaginationRepository<ChannelConfigEntity, String> {
}
