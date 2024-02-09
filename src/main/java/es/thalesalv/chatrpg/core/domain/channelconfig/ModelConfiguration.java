package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.thalesalv.chatrpg.common.exception.BusinessRuleViolationException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public final class ModelConfiguration {

    private final ArtificialIntelligenceModel aiModel;
    private final Integer maxTokenLimit;
    private final Integer messageHistorySize;
    private final Double temperature;
    private final Double frequencyPenalty;
    private final Double presencePenalty;
    private final List<String> stopSequences;
    private final Map<String, Double> logitBias;

    private ModelConfiguration(Builder builder) {

        this.aiModel = builder.aiModel;
        this.maxTokenLimit = builder.maxTokenLimit;
        this.messageHistorySize = builder.messageHistorySize;
        this.temperature = builder.temperature;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;

        this.stopSequences = Collections.unmodifiableList(
                builder.stopSequences == null ? Collections.emptyList() : builder.stopSequences);

        this.logitBias = Collections.unmodifiableMap(
                builder.logitBias == null ? Collections.emptyMap() : builder.logitBias);
    }

    public static Builder builder() {

        return new Builder();
    }

    private Builder cloneFrom(ModelConfiguration modelConfiguration) {

        return builder()
                .aiModel(modelConfiguration.getAiModel())
                .maxTokenLimit(modelConfiguration.getMaxTokenLimit())
                .messageHistorySize(modelConfiguration.getMessageHistorySize())
                .temperature(modelConfiguration.getTemperature())
                .frequencyPenalty(modelConfiguration.getFrequencyPenalty())
                .presencePenalty(modelConfiguration.getPresencePenalty())
                .stopSequences(modelConfiguration.getStopSequences())
                .logitBias(modelConfiguration.getLogitBias());
    }

    public ModelConfiguration updateAiModel(ArtificialIntelligenceModel aiModel) {

        return cloneFrom(this).aiModel(aiModel).build();
    }

    public ModelConfiguration updateMaxTokenLimit(Integer maxTokenLimit) {

        if (maxTokenLimit < 100 || maxTokenLimit > aiModel.getHardTokenLimit()) {
            throw new BusinessRuleViolationException(
                    String.format("Max token limit has to be between 100 and %s", aiModel.getHardTokenLimit()));
        }

        return cloneFrom(this).maxTokenLimit(maxTokenLimit).build();
    }

    public ModelConfiguration updateMessageHistorySize(Integer messageHistorySize) {

        if (messageHistorySize < 10 || messageHistorySize > 100) {
            throw new BusinessRuleViolationException("History size has to be between 10 and 100 messages");
        }

        return cloneFrom(this).messageHistorySize(messageHistorySize).build();
    }

    public ModelConfiguration updateTemperature(Double temperature) {

        if (temperature < 0.1 || temperature > 2) {
            throw new BusinessRuleViolationException("Temperature value has to be between 0 and 2");
        }

        return cloneFrom(this).temperature(temperature).build();
    }

    public ModelConfiguration updateFrequencyPenalty(Double frequencyPenalty) {

        if (frequencyPenalty < -2 || frequencyPenalty > 2) {
            throw new BusinessRuleViolationException("Frequency penalty needs to be between -2 and 2");
        }

        return cloneFrom(this).frequencyPenalty(frequencyPenalty).build();
    }

    public ModelConfiguration updatePresencePenalty(Double presencePenalty) {

        if (presencePenalty < -2 || presencePenalty > 2) {
            throw new BusinessRuleViolationException("Presence penalty needs to be between -2 and 2");
        }

        return cloneFrom(this).presencePenalty(presencePenalty).build();
    }

    public ModelConfiguration addStopSequence(String stopSequence) {

        List<String> stopSequences = new ArrayList<>(this.stopSequences);
        stopSequences.add(stopSequence);

        return cloneFrom(this).stopSequences(stopSequences).build();
    }

    public ModelConfiguration removeStopSequence(String stopSequence) {

        List<String> stopSequences = new ArrayList<>(this.stopSequences);
        stopSequences.remove(stopSequence);

        return cloneFrom(this).stopSequences(stopSequences).build();
    }

    public ModelConfiguration addLogitBias(String token, Double bias) {

        if (bias < -100 || bias > 100) {
            throw new BusinessRuleViolationException("Logit bias value needs to be between -100 and 100");
        }

        Map<String, Double> logitBias = new HashMap<>(this.logitBias);
        logitBias.put(token, bias);

        return cloneFrom(this).logitBias(logitBias).build();
    }

    public ModelConfiguration removeLogitBias(String token) {

        Map<String, Double> logitBias = new HashMap<>(this.logitBias);
        logitBias.remove(token);

        return cloneFrom(this).logitBias(logitBias).build();
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder {

        private static final double DEFAULT_FREQUENCY_PENALTY = 0.0;
        private static final double DEFAULT_PRESENCE_PENALTY = 0.0;

        private ArtificialIntelligenceModel aiModel;
        private Integer maxTokenLimit;
        private Integer messageHistorySize;
        private Double temperature;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private List<String> stopSequences;
        private Map<String, Double> logitBias;

        public Builder aiModel(ArtificialIntelligenceModel aiModel) {

            this.aiModel = aiModel;
            return this;
        }

        public Builder maxTokenLimit(Integer maxTokenLimit) {

            this.maxTokenLimit = maxTokenLimit;
            return this;
        }

        public Builder messageHistorySize(Integer messageHistorySize) {

            this.messageHistorySize = messageHistorySize;
            return this;
        }

        public Builder temperature(Double temperature) {

            this.temperature = temperature;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {

            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {

            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {

            this.stopSequences = stopSequences;
            return this;
        }

        public Builder logitBias(Map<String, Double> logitBias) {

            this.logitBias = logitBias;
            return this;
        }

        public ModelConfiguration build() {

            if (temperature < 0 || temperature > 2) {
                throw new BusinessRuleViolationException("Temperature value has to be between 0 and 2");
            }

            if (messageHistorySize < 10 || messageHistorySize > 100) {
                throw new BusinessRuleViolationException("History size has to be between 10 and 100 messages");
            }

            if (maxTokenLimit < 100 || maxTokenLimit > aiModel.getHardTokenLimit()) {
                throw new BusinessRuleViolationException(
                        String.format("Max token limit has to be between 100 and %s", aiModel.getHardTokenLimit()));
            }

            if (frequencyPenalty == null) {
                frequencyPenalty = DEFAULT_FREQUENCY_PENALTY;
            }

            if (frequencyPenalty < -2 || frequencyPenalty > 2) {
                throw new BusinessRuleViolationException("Frequency penalty needs to be between -2 and 2");
            }

            if (presencePenalty == null) {
                presencePenalty = DEFAULT_PRESENCE_PENALTY;
            }

            if (presencePenalty < -2 || presencePenalty > 2) {
                throw new BusinessRuleViolationException("Presence penalty needs to be between -2 and 2");
            }

            if (logitBias != null && !logitBias.isEmpty()) {
                boolean isLogitBiasRuleViolated = logitBias.entrySet().stream()
                        .anyMatch(entry -> entry.getValue() < -100 || entry.getValue() > 100);

                if (isLogitBiasRuleViolated) {
                    throw new BusinessRuleViolationException("Logit bias value needs to be between -100 and 100");
                }
            }

            return new ModelConfiguration(this);
        }
    }
}
