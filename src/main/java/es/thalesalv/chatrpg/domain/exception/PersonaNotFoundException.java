package es.thalesalv.chatrpg.domain.exception;

public class PersonaNotFoundException extends RuntimeException {

    public PersonaNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public PersonaNotFoundException(String msg) {

        super(msg);
    }

    public PersonaNotFoundException() {

        super();
    }
}
