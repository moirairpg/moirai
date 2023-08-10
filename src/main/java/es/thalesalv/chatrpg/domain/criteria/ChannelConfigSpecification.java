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

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(Root<ChannelConfigEntity> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        if (criteria.getOperation()
                .equalsIgnoreCase(">")) {
            return builder.greaterThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue()
                    .toString());
        } else if (criteria.getOperation()
                .equalsIgnoreCase("<")) {
            return builder.lessThanOrEqualTo(root.<String>get(criteria.getKey()), criteria.getValue()
                    .toString());
        } else if (criteria.getOperation()
                .equalsIgnoreCase(":")) {
            if (root.get(criteria.getKey())
                    .getJavaType() == String.class) {
                return builder.like(root.<String>get(criteria.getKey()), "%" + criteria.getValue() + "%");
            } else {
                return builder.equal(root.get(criteria.getKey()), criteria.getValue());
            }
        }
        return null;
    }
}
