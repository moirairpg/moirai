package es.thalesalv.chatrpg.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {

        super(message);
    }

    public BusinessException(String message, Throwable throwable) {

        super(message, throwable);
    }

    public BusinessException(Throwable throwable) {

        super(throwable);
    }
}
