package es.thalesalv.chatrpg.domain.exception;

public class DiscordAuthenticationException extends RuntimeException {

    public DiscordAuthenticationException(String msg, Throwable e) {

        super(msg, e);
    }

    public DiscordAuthenticationException(String msg) {

        super(msg);
    }
}