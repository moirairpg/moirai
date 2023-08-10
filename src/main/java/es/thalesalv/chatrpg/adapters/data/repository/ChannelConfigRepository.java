package es.thalesalv.chatrpg.adapters.data.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;

public interface ChannelConfigRepository extends JpaRepository<ChannelConfigEntity, String>,
        PagingAndSortingRepository<ChannelConfigEntity, String>, JpaSpecificationExecutor<ChannelConfigEntity> {

    Page<ChannelConfigEntity> findAll(Specification<ChannelConfigEntity> spec, Pageable pageable);
}
