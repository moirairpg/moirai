package es.thalesalv.chatrpg.domain.exception;

import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import lombok.Getter;

public class ErrorBotResponseException extends RuntimeException {

    @Getter
    private final CompletionResponse response;

    public ErrorBotResponseException(String msg, CompletionResponse response) {

        super(msg);
        this.response = response;
    }
}
