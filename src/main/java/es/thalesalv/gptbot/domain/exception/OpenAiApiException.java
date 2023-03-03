package es.thalesalv.gptbot.domain.exception;

import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.Getter;

@Getter
public class OpenAiApiException extends RuntimeException {

    private final GptResponse response;

    public OpenAiApiException(String msg, GptResponse response) {

        super(msg);
        this.response = response;
    }
}
