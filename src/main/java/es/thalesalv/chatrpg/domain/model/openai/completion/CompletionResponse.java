package es.thalesalv.chatrpg.domain.model.openai.completion;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompletionResponse {

    @JsonProperty("id")
    private String id;
    @JsonProperty("object")
    private String object;
    @JsonProperty("model")
    private String model;
    @JsonProperty("choices")
    private List<CompletionResponseChoice> choices;
    @JsonProperty("usage")
    private CompletionResponseUsage usage;
    @JsonProperty("error")
    private CompletionResponseError error;
    private String prompt;
}
