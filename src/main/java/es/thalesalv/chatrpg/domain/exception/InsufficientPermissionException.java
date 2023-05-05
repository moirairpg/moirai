package es.thalesalv.chatrpg.domain.exception;

public class InsufficientPermissionException extends RuntimeException {

    public InsufficientPermissionException(String message) {

        super(message);
    }
}
