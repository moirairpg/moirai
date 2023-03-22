package es.thalesalv.chatrpg.domain.exception;

public class AIModelNotSupportedException extends RuntimeException {

    public AIModelNotSupportedException(String msg, Throwable e) {
        super(msg, e);
    }

    public AIModelNotSupportedException(String msg) {
        super(msg);
    }
}
