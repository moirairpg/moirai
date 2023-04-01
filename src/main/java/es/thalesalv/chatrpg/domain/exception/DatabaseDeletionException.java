package es.thalesalv.chatrpg.domain.exception;

public class DatabaseDeletionException extends RuntimeException {

    public DatabaseDeletionException(String msg, Throwable e) {

        super(msg, e);
    }

    public DatabaseDeletionException(String msg) {

        super(msg);
    }

    public DatabaseDeletionException() {

        super();
    }
}
