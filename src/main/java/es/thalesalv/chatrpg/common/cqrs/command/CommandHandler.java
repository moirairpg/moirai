package es.thalesalv.chatrpg.common.cqrs.command;

import java.util.Objects;

public interface CommandHandler<A extends Command<T>, T> {

    T handle(A command);

    default void validate(A command) {

    }

    default T execute(A command) {

        if (Objects.isNull(command)) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        validate(command);
        return handle(command);
    }
}
