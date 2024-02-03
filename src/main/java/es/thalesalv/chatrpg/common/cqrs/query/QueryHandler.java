package es.thalesalv.chatrpg.common.cqrs.query;

import java.util.Objects;

public interface QueryHandler<A extends Query<T>, T> {

    T handle(A query);

    default void validate(A query) {

    }

    default T execute(A query) {

        if (Objects.isNull(query)) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        validate(query);
        return handle(query);
    }
}
