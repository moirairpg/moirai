package es.thalesalv.chatrpg.domain.exception;

public class LorebookNotFoundException extends RuntimeException {

    public LorebookNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public LorebookNotFoundException(String msg) {

        super(msg);
    }

    public LorebookNotFoundException() {

        super();
    }
}
