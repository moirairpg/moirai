package es.thalesalv.chatrpg.infrastructure.outbound.adapter.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponseChoice {

    @JsonProperty("text")
    private String text;

    @JsonProperty("finish_reason")
    private String finishReason;

    @JsonProperty("index")
    private int index;

    @JsonProperty("message")
    private ChatMessage message;
}