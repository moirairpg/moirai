package es.thalesalv.gptbot.domain.exception;

import lombok.Getter;

public class ModerationException extends RuntimeException {

    @Getter
    private final String prompt;

    public ModerationException(String msg, String prompt) {

        super(msg);
        this.prompt = prompt;
    }
}
