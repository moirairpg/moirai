package es.thalesalv.chatrpg.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class DiscordApiException extends RuntimeException {

    private HttpStatus httpStatusCode;
    private String errorMessage;
    private String errorDescription;

    public DiscordApiException() {
        super();
    }

    public DiscordApiException(String msg) {
        super(msg);
    }

    public DiscordApiException(String msg, Throwable t) {
        super(msg, t);
    }

    public DiscordApiException(HttpStatus httpStatusCode, String errorMessage, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message, Throwable t) {

        super(message, t);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public DiscordApiException(HttpStatus httpStatusCode, String message, Throwable t) {

        super(message, t);

        this.httpStatusCode = httpStatusCode;
    }
}
