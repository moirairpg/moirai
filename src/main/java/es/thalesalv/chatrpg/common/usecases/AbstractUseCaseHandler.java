package es.thalesalv.chatrpg.common.usecases;

import java.util.Objects;

import es.thalesalv.chatrpg.common.annotation.UseCaseHandler;

@UseCaseHandler
public abstract class AbstractUseCaseHandler<A extends UseCase<T>, T> {

    public abstract T execute(A useCase);

    public void validate(A useCase) {

    }

    public T handle(A useCase) {

        if (Objects.isNull(useCase)) {
            throw new IllegalArgumentException("Use case cannot be null");
        }

        validate(useCase);
        return execute(useCase);
    }
}
