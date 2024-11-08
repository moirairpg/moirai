package me.moirai.discordbot.core.domain.channelconfig;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

import me.moirai.discordbot.common.exception.AIModelNotSupportedException;

public enum ArtificialIntelligenceModel {

    @JsonProperty("gpt35-turbo")
    GPT35_TURBO("gpt35-turbo", "gpt-3.5-turbo", 16385),

    @JsonProperty("gpt4-mini")
    GPT4_MINI("gpt4-mini", "gpt-4o-mini", 128000),

    @JsonProperty("gpt4-omni")
    GPT4_OMNI("gpt4-omni", "gpt-4o", 128000);

    private final String internalModelName;
    private final String officialModelName;
    private final int hardTokenLimit;

    private ArtificialIntelligenceModel(String internalModelName, String officialModelName, int hardTokenLimit) {
        this.internalModelName = internalModelName;
        this.officialModelName = officialModelName;
        this.hardTokenLimit = hardTokenLimit;
    }

    public String getInternalModelName() {
        return internalModelName;
    }

    public String getOfficialModelName() {
        return officialModelName;
    }

    public int getHardTokenLimit() {
        return hardTokenLimit;
    }

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
