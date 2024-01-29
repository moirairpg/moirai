package es.thalesalv.chatrpg.common.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {

        super(message);
    }

    public BusinessRuleViolationException(String message, Throwable throwable) {

        super(message, throwable);
    }

    public BusinessRuleViolationException(Throwable throwable) {

        super(throwable);
    }
}
