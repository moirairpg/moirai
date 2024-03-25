package es.thalesalv.chatrpg.common.exception;

import lombok.Getter;

@Getter
public class AuthenticationFailedException extends RuntimeException {

    private String responseMessage;

    public AuthenticationFailedException(String responseMessage, String msg) {
        super(msg + ": " + responseMessage);

        this.responseMessage = responseMessage;
    }
}
