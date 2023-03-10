package es.thalesalv.gptbot.domain.exception;

public class DiscordFunctionException extends RuntimeException {

    public DiscordFunctionException(String msg, Throwable e) {
        super(msg, e);
    }

    public DiscordFunctionException(String msg) {
        super(msg);
    }
}
