package me.moirai.discordbot.core.domain.adventure;

import java.util.Arrays;

import me.moirai.discordbot.common.exception.AIModelNotSupportedException;

public enum ArtificialIntelligenceModel {

    GPT35_TURBO("GPT-3.5 Turbo", "gpt35-turbo", "gpt-3.5-turbo", 16385),
    GPT4_MINI("GPT-4 Mini", "gpt4-mini", "gpt-4o-mini", 128000),
    GPT4_OMNI("GPT-4 Omni", "gpt4-omni", "gpt-4o", 128000);

    private final String fullModelName;
    private final String internalModelName;
    private final String officialModelName;
    private final int hardTokenLimit;

    private ArtificialIntelligenceModel(
            String fullModelName,
            String internalModelName,
            String officialModelName,
            int hardTokenLimit) {

        this.fullModelName = fullModelName;
        this.internalModelName = internalModelName;
        this.officialModelName = officialModelName;
        this.hardTokenLimit = hardTokenLimit;
    }

    public String getFullModelName() {
        return fullModelName;
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

    public static ArtificialIntelligenceModel fromInternalName(String internalModelName) {

        return Arrays.stream(values())
                .filter(aiModel -> aiModel.getInternalModelName().equals(internalModelName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new AIModelNotSupportedException("Unsupported model: " + internalModelName));
    }
}
