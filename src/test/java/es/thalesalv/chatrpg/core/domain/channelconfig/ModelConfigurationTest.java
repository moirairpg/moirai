package es.thalesalv.chatrpg.core.domain.channelconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;

public class ModelConfigurationTest {

    @Test
    public void createModelConfiguration() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfiguration.builder();
        modelConfigurationBuilder.aiModel(ArtificialIntelligenceModel.GPT35_16K);
        modelConfigurationBuilder.frequencyPenalty(0.2);
        modelConfigurationBuilder.presencePenalty(0.2);
        modelConfigurationBuilder.maxTokenLimit(100);
        modelConfigurationBuilder.messageHistorySize(25);
        modelConfigurationBuilder.temperature(1.0);

        Map<String, Double> logitBias = new HashMap<>();
        logitBias.put("ABC", 50.0);
        logitBias.put("DEF", 5.0);

        List<String> stopSequences = new ArrayList<>();
        stopSequences.add("ABC");

        modelConfigurationBuilder.logitBias(logitBias);
        modelConfigurationBuilder.stopSequences(stopSequences);

        // When
        ModelConfiguration modelConfiguration = modelConfigurationBuilder.build();

        // Then
        assertThat(modelConfiguration).isNotNull();
        assertThat(modelConfiguration.getAiModel()).isEqualTo(ArtificialIntelligenceModel.GPT35_16K);
        assertThat(modelConfiguration.getFrequencyPenalty()).isEqualTo(0.2);
        assertThat(modelConfiguration.getPresencePenalty()).isEqualTo(0.2);
        assertThat(modelConfiguration.getMaxTokenLimit()).isEqualTo(100);
        assertThat(modelConfiguration.getMessageHistorySize()).isEqualTo(25);
        assertThat(modelConfiguration.getTemperature()).isEqualTo(1.0);
    }

    @Test
    public void updateAiModel() {

        // Given
        ArtificialIntelligenceModel newModel = ArtificialIntelligenceModel.GPT4_128K;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updateAiModel(newModel);

        // Then
        assertThat(newModelConfiguration.getAiModel()).isEqualTo(newModel);
    }

    @Test
    public void updateMaxTokenLimit() {

        // Given
        Integer newTokenLimit = 700;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updateMaxTokenLimit(newTokenLimit);

        // Then
        assertThat(newModelConfiguration.getMaxTokenLimit()).isEqualTo(newTokenLimit);
    }

    @Test
    public void updateMessageHistorySize() {

        // Given
        Integer newHistorySize = 77;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updateMessageHistorySize(newHistorySize);

        // Then
        assertThat(newModelConfiguration.getMessageHistorySize()).isEqualTo(newHistorySize);
    }

    @Test
    public void updateTemperature() {

        // Given
        Double newTemperature = 1.7;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updateTemperature(newTemperature);

        // Then
        assertThat(newModelConfiguration.getTemperature()).isEqualTo(newTemperature);
    }

    @Test
    public void updateFrequencyPenalty() {

        // Given
        Double newFrequencyPenalty = 1.7;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updateFrequencyPenalty(newFrequencyPenalty);

        // Then
        assertThat(newModelConfiguration.getFrequencyPenalty()).isEqualTo(newFrequencyPenalty);
    }

    @Test
    public void updatePresencePenalty() {

        // Given
        Double newPresencePenalty = 1.7;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.updatePresencePenalty(newPresencePenalty);

        // Then
        assertThat(newModelConfiguration.getPresencePenalty()).isEqualTo(newPresencePenalty);
    }

    @Test
    public void addLogitBias() {

        // Given
        String newToken = "323";
        Double bias = 57.0;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.addLogitBias(newToken, bias);

        // Then
        assertThat(newModelConfiguration.getLogitBias()).containsKey(newToken);
    }

    @Test
    public void removeLogitBias() {

        // Given
        String newToken = "323";
        Double bias = 57.0;
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();
        modelConfiguration = modelConfiguration.addLogitBias(newToken, bias);

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.removeLogitBias(newToken);

        // Then
        assertThat(newModelConfiguration.getLogitBias()).doesNotContainKey(newToken);
    }

    @Test
    public void addStopSequence() {

        // Given
        String newToken = "323";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.addStopSequence(newToken);

        // Then
        assertThat(newModelConfiguration.getStopSequences()).contains(newToken);
    }

    @Test
    public void removeStopSequence() {

        // Given
        String newToken = "323";
        ModelConfiguration modelConfiguration = ModelConfigurationFixture.sample().build();
        modelConfiguration = modelConfiguration.addStopSequence(newToken);

        // When
        ModelConfiguration newModelConfiguration = modelConfiguration.removeStopSequence(newToken);

        // Then
        assertThat(newModelConfiguration.getStopSequences()).doesNotContain(newToken);
    }

    @Test
    public void errorWhenTemperatureIsHigherThanLimit() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .temperature(3.0);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenTemperatureIsLowerThanLimit() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .temperature(-3.0);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenMessageHistorySizeIsHigherThanLimit() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .messageHistorySize(200);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenMessageHistorySizeIsLowerThanLimit() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .messageHistorySize(5);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenMaxTokenLimitIsLowerThanLimit() {

        // Given
        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .maxTokenLimit(5);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenMaxTokenLimitIsHigherThanModelLimit() {

        // Given
        ModelConfiguration.Builder gpt3516k = ModelConfigurationFixture.sample()
                .aiModel(ArtificialIntelligenceModel.GPT35_16K)
                .maxTokenLimit(20000);

        ModelConfiguration.Builder gpt354k = ModelConfigurationFixture.sample()
                .aiModel(ArtificialIntelligenceModel.GPT35_4K)
                .maxTokenLimit(5000);

        // Then
        assertThrows(BusinessRuleViolationException.class, gpt3516k::build);
        assertThrows(BusinessRuleViolationException.class, gpt354k::build);
    }

    @Test
    public void errorWhenLogitBiasIsLowerThanLimit() {

        // Given
        Map<String, Double> logitBias = new HashMap<>();
        logitBias.put("ABC", -200.0);

        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .logitBias(logitBias);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void errorWhenLogitBiasIsHigherThanLimit() {

        // Given
        Map<String, Double> logitBias = new HashMap<>();
        logitBias.put("ABC", 200.0);

        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .logitBias(logitBias);

        // Then
        assertThrows(BusinessRuleViolationException.class, modelConfigurationBuilder::build);
    }

    @Test
    public void createModelConfigurationWithEmptyLogitBias() {

        // Given
        Map<String, Double> logitBias = Collections.emptyMap();

        ModelConfiguration.Builder modelConfigurationBuilder = ModelConfigurationFixture.sample()
                .logitBias(logitBias);

        // When
        ModelConfiguration modelConfiguration = modelConfigurationBuilder.build();

        // Then
        assertThat(modelConfiguration).isNotNull();
        assertThat(modelConfiguration.getLogitBias()).isNotNull().isEmpty();
    }
}
