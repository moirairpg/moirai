package es.thalesalv.chatrpg.domain.exception;

public class ModelSettingsNotFoundException extends RuntimeException {

    public ModelSettingsNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public ModelSettingsNotFoundException(String msg) {

        super(msg);
    }

    public ModelSettingsNotFoundException() {

        super();
    }
}
