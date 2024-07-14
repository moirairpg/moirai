package me.moirai.discordbot.infrastructure.outbound.persistence.channelconfig;

import java.util.List;
import java.util.Map;

import me.moirai.discordbot.common.dbutil.StringListConverter;
import me.moirai.discordbot.common.dbutil.StringMapDoubleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;

@Embeddable
public class ModelConfigurationEntity {

    @Column(name = "ai_model", nullable = false)
    private String aiModel;

    @Column(name = "max_token_limit", nullable = false)
    private int maxTokenLimit;

    @Column(name = "message_history_size", nullable = false)
    private int messageHistorySize;

    @Column(name = "temperature", nullable = false)
    private Double temperature;

    @Column(name = "frequency_penalty", nullable = false)
    private Double frequencyPenalty;

    @Column(name = "presence_penalty", nullable = false)
    private Double presencePenalty;

    @Column(name = "stop_sequences")
    @Convert(converter = StringListConverter.class)
    private List<String> stopSequences;

    @Column(name = "logit_bias")
    @Convert(converter = StringMapDoubleConverter.class)
    private Map<String, Double> logitBias;

    protected ModelConfigurationEntity() {
    }

    private ModelConfigurationEntity(Builder builder) {
        this.aiModel = builder.aiModel;
        this.maxTokenLimit = builder.maxTokenLimit;
        this.messageHistorySize = builder.messageHistorySize;
        this.temperature = builder.temperature;
        this.frequencyPenalty = builder.frequencyPenalty;
        this.presencePenalty = builder.presencePenalty;
        this.stopSequences = builder.stopSequences;
        this.logitBias = builder.logitBias;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getAiModel() {
        return aiModel;
    }

    public int getMaxTokenLimit() {
        return maxTokenLimit;
    }

    public int getMessageHistorySize() {
        return messageHistorySize;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getFrequencyPenalty() {
        return frequencyPenalty;
    }

    public Double getPresencePenalty() {
        return presencePenalty;
    }

    public List<String> getStopSequences() {
        return stopSequences;
    }

    public Map<String, Double> getLogitBias() {
        return logitBias;
    }

    public static final class Builder {

        private String aiModel;
        private int maxTokenLimit;
        private int messageHistorySize;
        private Double temperature;
        private Double frequencyPenalty;
        private Double presencePenalty;
        private List<String> stopSequences;
        private Map<String, Double> logitBias;

        private Builder() {
        }

        public Builder aiModel(String aiModel) {
            this.aiModel = aiModel;
            return this;
        }

        public Builder maxTokenLimit(int maxTokenLimit) {
            this.maxTokenLimit = maxTokenLimit;
            return this;
        }

        public Builder messageHistorySize(int messageHistorySize) {
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

        public ModelConfigurationEntity build() {
            return new ModelConfigurationEntity(this);
        }
    }
}
