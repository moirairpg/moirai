package es.thalesalv.chatrpg.domain.exception;

public class ModerationSettingsNotFoundException extends RuntimeException {

    public ModerationSettingsNotFoundException(String msg, Throwable e) {

        super(msg, e);
    }

    public ModerationSettingsNotFoundException(String msg) {

        super(msg);
    }

    public ModerationSettingsNotFoundException() {

        super();
    }
}
