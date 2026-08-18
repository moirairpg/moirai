package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiResponsesApiRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("instructions")
    private String instructions;

    @JsonProperty("input")
    private List<OpenAiInputMessage> input;

    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;

    @JsonProperty("temperature")
    private Double temperature;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("text")
    private OpenAiTextOptions text;

    public OpenAiResponsesApiRequest() {
    }

    private OpenAiResponsesApiRequest(Builder builder) {

        this.model = builder.model;
        this.instructions = builder.instructions;
        this.input = builder.input;
        this.maxOutputTokens = builder.maxOutputTokens;
        this.temperature = builder.temperature;
        this.text = builder.text;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getModel() {
        return model;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<OpenAiInputMessage> getInput() {
        return input;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public OpenAiTextOptions getText() {
        return text;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setInput(List<OpenAiInputMessage> input) {
        this.input = input;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public void setText(OpenAiTextOptions text) {
        this.text = text;
    }

    public static final class Builder {

        private String model;
        private String instructions;
        private List<OpenAiInputMessage> input;
        private Integer maxOutputTokens;
        private Double temperature;
        private OpenAiTextOptions text;

        private Builder() {
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public Builder instructions(String instructions) {
            this.instructions = instructions;
            return this;
        }

        public Builder input(List<OpenAiInputMessage> input) {
            this.input = input;
            return this;
        }

        public Builder maxOutputTokens(Integer maxOutputTokens) {
            this.maxOutputTokens = maxOutputTokens;
            return this;
        }

        public Builder temperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder text(OpenAiTextOptions text) {
            this.text = text;
            return this;
        }

        public OpenAiResponsesApiRequest build() {
            return new OpenAiResponsesApiRequest(this);
        }
    }
}
