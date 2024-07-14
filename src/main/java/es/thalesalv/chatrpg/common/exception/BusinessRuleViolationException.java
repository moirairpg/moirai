package es.thalesalv.chatrpg.common.exception;

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
