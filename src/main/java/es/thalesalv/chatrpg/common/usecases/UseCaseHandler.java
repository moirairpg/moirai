package es.thalesalv.chatrpg.common.usecases;

import java.util.Objects;

public abstract class UseCaseHandler<A extends UseCase<T>, T> {

    public abstract T execute(A command);

    public void validate(A command) {

    }

    public T handle(A command) {

        if (Objects.isNull(command)) {
            throw new IllegalArgumentException("Use case cannot be null");
        }

        validate(command);
        return execute(command);
    }
}
