package es.thalesalv.chatrpg.infrastructure.outbound.persistence.channelconfig;

import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfigurationFixture;

public class ModelConfigurationEntityFixture {

    public static ModelConfigurationEntity.Builder gpt3516k() {

        ModelConfiguration modelConfiguration = ModelConfigurationFixture.gpt3516k().build();
        return ModelConfigurationEntity.builder()
                .aiModel(modelConfiguration.getAiModel().getInternalModelName())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .logitBias(modelConfiguration.getLogitBias())
                .maxTokenLimit(modelConfiguration.getMaxTokenLimit())
                .messageHistorySize(modelConfiguration.getMessageHistorySize())
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
                .messageHistorySize(modelConfiguration.getMessageHistorySize())
                .stopSequences(modelConfiguration.getStopSequences())
                .temperature(modelConfiguration.getTemperature());
    }
}
