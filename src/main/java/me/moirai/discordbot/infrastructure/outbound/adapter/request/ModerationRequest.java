package me.moirai.discordbot.infrastructure.outbound.adapter.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModerationRequest {

    @JsonProperty("input")
    private String input;

    public ModerationRequest() {
    }

    private ModerationRequest(String input) {
        this.input = input;
    }

    public static ModerationRequest build(String input) {

        return new ModerationRequest(input);
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
}