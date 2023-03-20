package es.thalesalv.chatrpg.domain.exception;

import es.thalesalv.chatrpg.domain.model.openai.completion.CompletionResponse;
import lombok.Getter;

@Getter
public class OpenAiApiException extends RuntimeException {

    private final CompletionResponse response;

    public OpenAiApiException(String msg, CompletionResponse response) {

        super(msg);
        this.response = response;
    }
}
