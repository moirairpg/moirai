package es.thalesalv.chatrpg.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class OpenAiApiException extends RuntimeException {

    private final HttpStatus httpStatusCode;
    private final String errorMessage;
    private final String errorDescription;

    public OpenAiApiException(HttpStatus httpStatusCode, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = null;
        this.errorDescription = null;
    }

    public OpenAiApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message) {

        super(message);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }

    public OpenAiApiException(HttpStatus httpStatusCode, String errorMessage,
            String errorDescription, String message, Throwable t) {

        super(message, t);

        this.httpStatusCode = httpStatusCode;
        this.errorMessage = errorMessage;
        this.errorDescription = errorDescription;
    }
}
