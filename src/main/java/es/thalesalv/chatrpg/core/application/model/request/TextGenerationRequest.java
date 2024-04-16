package es.thalesalv.chatrpg.core.application.model.request;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public final class TextGenerationRequest {

    private final String model;
    private final List<ChatMessage> messages;
    private final List<String> stopSequences;
    private final Integer maxTokens;
    private final Double temperature;
    private final Double presencePenalty;
    private final Double frequencyPenalty;
    private final Map<String, Double> logitBias;

    public TextGenerationRequest(Builder builder) {

        this.model = builder.model;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.presencePenalty = builder.presencePenalty;
        this.frequencyPenalty = builder.frequencyPenalty;

        this.messages = unmodifiableList(
                builder.messages == null ? emptyList() : new ArrayList<>(builder.messages));

        this.stopSequences = unmodifiableList(
                builder.stopSequences == null ? emptyList() : new ArrayList<>(builder.stopSequences));

        this.logitBias = unmodifiableMap(
                builder.logitBias == null ? emptyMap() : new HashMap<>(builder.logitBias));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String model;
        private List<ChatMessage> messages;
        private List<String> stopSequences;
        private Integer maxTokens;
        private Double temperature;
        private Double presencePenalty;
        private Double frequencyPenalty;
        private Map<String, Double> logitBias;

        public Builder model(String model) {

            this.model = model;
            return this;
        }

        public Builder messages(List<ChatMessage> messages) {

            this.messages = messages;
            return this;
        }

        public Builder stopSequences(List<String> stopSequences) {

            this.stopSequences = stopSequences;
            return this;
        }

        public Builder maxTokens(Integer maxTokens) {

            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(Double temperature) {

            this.temperature = temperature;
            return this;
        }

        public Builder presencePenalty(Double presencePenalty) {

            this.presencePenalty = presencePenalty;
            return this;
        }

        public Builder frequencyPenalty(Double frequencyPenalty) {

            this.frequencyPenalty = frequencyPenalty;
            return this;
        }

        public Builder logitBias(Map<String, Double> logitBias) {

            this.logitBias = logitBias;
            return this;
        }

        public TextGenerationRequest build() {

            return new TextGenerationRequest(this);
        }
    }
}
