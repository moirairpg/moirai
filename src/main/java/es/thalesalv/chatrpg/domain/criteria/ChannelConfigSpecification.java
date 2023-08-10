package es.thalesalv.chatrpg.domain.criteria;

import org.springframework.data.jpa.domain.Specification;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelConfigEntity;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ChannelConfigSpecification implements Specification<ChannelConfigEntity> {

    private String fieldName;
    private String valueToSearch;

    @Override
    public Predicate toPredicate(Root<ChannelConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        return builder.like(root.<String>get(fieldName), "%" + valueToSearch + "%");
    }
}
