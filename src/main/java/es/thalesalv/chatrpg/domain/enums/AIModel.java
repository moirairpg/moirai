package es.thalesalv.chatrpg.domain.enums;

import java.util.Arrays;

import es.thalesalv.chatrpg.domain.exception.AIModelNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AIModel {

    ADA("ada", "text-ada-001", "textCompletionService", 2048),
    CURIE("curie", "text-curie-001", "textCompletionService", 2048),
    BABBAGE("babbage", "text-babbage-001", "textCompletionService", 2048),
    DAVINCI("davinci", "text-davinci-003", "textCompletionService", 4096),
    CHATGPT("chatgpt", "gpt-3.5-turbo", "chatCompletionService", 4096),
    GPT4("gpt4", "gpt-4", "chatCompletionService", 8192),
    GPT432K("gpt432k", "gpt-4-32k", "chatCompletionService", 32768);

    private final String internalName;
    private final String modelName;
    private final String completionType;
    private final int tokenCap;

    public static AIModel findByInternalName(final String name) {

        return Arrays.stream(values())
                .filter(aiModel -> aiModel.getInternalName()
                        .equals(name))
                .findFirst()
                .orElseThrow(() -> new AIModelNotSupportedException("The specified model is not supported."));
    }
}
