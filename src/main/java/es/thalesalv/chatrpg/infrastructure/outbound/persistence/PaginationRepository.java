package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;

@NoRepositoryBean
public interface PaginationRepository<T, U> extends PagingAndSortingRepository<T, U>, JpaSpecificationExecutor<T> {

    @NonNull Page<T> findAll(@NonNull Specification<T> spec, @NonNull Pageable pageable);
}
