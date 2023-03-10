package es.thalesalv.chatrpg.domain.exception;

public class DiscordFunctionException extends RuntimeException {

    public DiscordFunctionException(String msg, Throwable e) {
        super(msg, e);
    }

    public DiscordFunctionException(String msg) {
        super(msg);
    }
}
