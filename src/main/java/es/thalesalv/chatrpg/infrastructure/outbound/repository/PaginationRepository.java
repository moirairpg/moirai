package es.thalesalv.chatrpg.infrastructure.outbound.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

@NoRepositoryBean
public interface PaginationRepository<T, U> extends PagingAndSortingRepository<T, U>, JpaSpecificationExecutor<T> {

    Page<T> findAll(Specification<T> spec, Pageable pageable);
}
