package es.thalesalv.chatrpg.domain.exception;

public class LorebookEntryNotFoundException extends RuntimeException {

    public LorebookEntryNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public LorebookEntryNotFoundException(String msg) {

        super(msg);
    }

    public LorebookEntryNotFoundException() {

        super();
    }
}
