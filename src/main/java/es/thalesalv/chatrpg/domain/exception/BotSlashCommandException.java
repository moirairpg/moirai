package es.thalesalv.chatrpg.domain.exception;

public class BotSlashCommandException extends RuntimeException {

    public BotSlashCommandException(String msg, Exception e) {
        super(msg, e);
    }

    public BotSlashCommandException(Exception e) {
        super(e);
    }

    public BotSlashCommandException(String msg) {
        super(msg);
    }
}
