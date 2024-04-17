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

    @JsonProperty("gpt35-16k")
    GPT35_16K("gpt35-16k", "gpt-3.5-turbo", 16385),

    @JsonProperty("gpt4-128k")
    GPT4_128K("gpt4-128k", "gpt-4-turbo", 128000);

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
