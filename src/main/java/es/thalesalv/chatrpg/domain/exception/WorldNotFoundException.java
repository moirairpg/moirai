package es.thalesalv.chatrpg.domain.exception;

public class WorldNotFoundException extends RuntimeException {

    public WorldNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public WorldNotFoundException(String msg) {

        super(msg);
    }

    public WorldNotFoundException() {

        super();
    }
}