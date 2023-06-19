package es.thalesalv.chatrpg.domain.enums;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.thalesalv.chatrpg.domain.exception.AIModelNotSupportedException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AIModel {

    @JsonProperty("ada")
    ADA("ada", "text-ada-001", "textCompletionService", 2048),

    @JsonProperty("curie")
    CURIE("curie", "text-curie-001", "textCompletionService", 2048),

    @JsonProperty("babbage")
    BABBAGE("babbage", "text-babbage-001", "textCompletionService", 2048),

    @JsonProperty("davinci")
    DAVINCI("davinci", "text-davinci-003", "textCompletionService", 4096),

    @JsonProperty("chatgpt")
    CHATGPT("chatgpt", "gpt-3.5-turbo", "chatCompletionService", 4096),

    @JsonProperty("chatgpt16k")
    CHATGPT16K("chatgpt16k", "gpt-3.5-turbo-16k-0613", "chatCompletionService", 16386),

    @JsonProperty("gpt4")
    GPT4("gpt4", "gpt-4", "chatCompletionService", 8192),

    @JsonProperty("gpt432k")
    GPT432K("gpt432k", "gpt-4-32k", "chatCompletionService", 32768);

    private final String internalName;
    private final String modelName;
    private final String completionType;
    private final int tokenCap;

    @Override
    public String toString() {

        return internalName;
    }

    public static AIModel findByInternalName(final String name) {

        return Arrays.stream(values())
                .filter(aiModel -> aiModel.getInternalName()
                        .equals(name))
                .findFirst()
                .orElseThrow(() -> new AIModelNotSupportedException("Unsupported model: " + name));
    }
}
