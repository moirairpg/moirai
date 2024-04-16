package es.thalesalv.chatrpg.core.application.model.result;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public final class TextGenerationResult {

    private final String outputText;
    private final Integer promptTokens;
    private final Integer completionTokens;
    private final Integer totalTokens;
}
