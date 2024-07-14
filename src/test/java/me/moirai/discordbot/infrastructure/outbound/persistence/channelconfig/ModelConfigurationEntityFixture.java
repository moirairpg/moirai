package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import me.moirai.discordbot.core.domain.channelconfig.ModelConfiguration;
import me.moirai.discordbot.core.domain.channelconfig.ModelConfigurationFixture;

public class ModelConfigurationEntityFixture {

    public static ModelConfigurationEntity.Builder gpt3516k() {

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        return ModelConfigurationEntity.builder()
                .aiModel(modelConfiguration.getAiModel().getInternalModelName())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .logitBias(modelConfiguration.getLogitBias())
                .maxTokenLimit(modelConfiguration.getMaxTokenLimit())
                .stopSequences(modelConfiguration.getStopSequences())
                .temperature(modelConfiguration.getTemperature());
    }

    public static ModelConfigurationEntity.Builder gpt4128k() {

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt4128k().build();
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
