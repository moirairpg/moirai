package es.thalesalv.chatrpg.domain.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public NotFoundException(String msg) {

        super(msg);
    }

    public NotFoundException() {

        super();
    }
}
