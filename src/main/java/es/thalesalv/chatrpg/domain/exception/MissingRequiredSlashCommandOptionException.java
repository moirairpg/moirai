package es.thalesalv.chatrpg.domain.exception;

public class MissingRequiredSlashCommandOptionException extends RuntimeException {

    public MissingRequiredSlashCommandOptionException() {

        super();
    }

    public MissingRequiredSlashCommandOptionException(final String msg) {

        super(msg);
    }
}
