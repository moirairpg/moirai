package me.moirai.storyengine.infrastructure.outbound.adapter.generation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenAiTextOptions {

    @JsonProperty("format")
    private OpenAiTextFormat format;

    public OpenAiTextOptions() {
    }

    public OpenAiTextOptions(OpenAiTextFormat format) {
        this.format = format;
    }

    public OpenAiTextFormat getFormat() {
        return format;
    }

    public void setFormat(OpenAiTextFormat format) {
        this.format = format;
    }
}
