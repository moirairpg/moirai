package me.moirai.discordbot.common.exception;

public class AIModelNotSupportedException extends BusinessRuleViolationException {

    public AIModelNotSupportedException(String message) {

        super(message);
    }
}
