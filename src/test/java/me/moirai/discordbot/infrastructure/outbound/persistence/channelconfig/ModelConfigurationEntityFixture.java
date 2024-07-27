package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfigurationFixture;

public class ModelConfigurationEntityFixture {

    public static ModelConfigurationEntity.Builder gpt4Mini() {

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt4Mini().build();
        return ModelConfigurationEntity.builder()
                .aiModel(modelConfiguration.getAiModel().getInternalModelName())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .logitBias(modelConfiguration.getLogitBias())
                .maxTokenLimit(modelConfiguration.getMaxTokenLimit())
                .stopSequences(modelConfiguration.getStopSequences())
                .temperature(modelConfiguration.getTemperature());
    }

    public static ModelConfigurationEntity.Builder gpt4Omni() {

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt4Omni().build();
        return ModelConfigurationEntity.builder()
                .aiModel(modelConfiguration.getAiModel().getInternalModelName())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .logitBias(modelConfiguration.getLogitBias())
                .maxTokenLimit(modelConfiguration.getMaxTokenLimit())
                .stopSequences(modelConfiguration.getStopSequences())
                .temperature(modelConfiguration.getTemperature());
    }
}
