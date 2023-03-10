package es.thalesalv.chatrpg.domain.exception;

import es.thalesalv.chatrpg.domain.model.openai.gpt.GptResponse;
import lombok.Getter;

@Getter
public class OpenAiApiException extends RuntimeException {

    private final GptResponse response;

    public OpenAiApiException(String msg, GptResponse response) {

        super(msg);
        this.response = response;
    }
}
