package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.thalesalv.chatrpg.common.exception.AIModelNotSupportedException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ArtificialIntelligenceModel {

    /**
     * @deprecated
     * This model is no longer supported by OpenAI and will be removed from ChatRPG.
     */
    @Deprecated
    @JsonProperty("gpt35-4k")
    GPT35_4K("gpt35-4k", "gpt-3.5-turbo", 4096),

    @JsonProperty("gpt35-16k")
    GPT35_16K("gpt35-16k", "gpt-3.5-turbo-16k", 16386),

    /**
     * @deprecated
     * This is deprecated and will be removed from ChatRPG.
     */
    @Deprecated
    @JsonProperty("gpt4-8k")
    GPT4_8K("gpt4-8k", "gpt-4", 8192),

    /**
     * @deprecated
     * This is deprecated and will be removed from ChatRPG.
     */
    @Deprecated
    @JsonProperty("gpt4-32k")
    GPT4_32K("gpt4-32k", "gpt-4-32k", 32768),

    @JsonProperty("gpt4-128k")
    GPT4_128K("gpt4-128k", "gpt-4-turbo-preview", 128000);

    private final String internalModelName;
    private final String officialModelName;
    private final int hardTokenLimit;

    @Override
    public String toString() {

        return internalModelName;
    }

    public static ArtificialIntelligenceModel findByInternalModelName(String internalModelName) {

        return Arrays.stream(values())
                .filter(aiModel -> aiModel.getInternalModelName().equals(internalModelName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AIModelNotSupportedException("Unsupported model: " + internalModelName));
    }
}
