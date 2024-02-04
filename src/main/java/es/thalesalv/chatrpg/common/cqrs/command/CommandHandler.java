package es.thalesalv.chatrpg.common.cqrs.command;

import java.util.Objects;

public abstract class CommandHandler<A extends Command<T>, T> {

    public abstract T handle(A command);

    public void validate(A command) {

    }

    public T execute(A command) {

        if (Objects.isNull(command)) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        validate(command);
        return handle(command);
    }
}
