package es.thalesalv.chatrpg.common.cqrs.query;

import java.util.Objects;

public abstract class QueryHandler<A extends Query<T>, T> {

    public abstract T handle(A query);

    void validate(A query) {

    }

    T execute(A query) {

        if (Objects.isNull(query)) {
            throw new IllegalArgumentException("Query cannot be null");
        }

        validate(query);
        return handle(query);
    }
}
