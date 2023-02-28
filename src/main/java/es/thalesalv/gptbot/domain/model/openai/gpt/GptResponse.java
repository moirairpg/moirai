package es.thalesalv.gptbot.domain.model.openai.gpt;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GptResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<GptModelResponseChoice> choices;

    @JsonProperty("usage")
    private GptModelResponseUsage usage;

    @JsonProperty("error")
    private GptModelResponseError error;

    private String prompt;
}
