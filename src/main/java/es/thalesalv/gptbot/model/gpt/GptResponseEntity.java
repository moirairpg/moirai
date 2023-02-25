package es.thalesalv.gptbot.model.gpt;

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
public class GptResponseEntity {

    @JsonProperty("id")
    private String id;

    @JsonProperty("object")
    private String object;

    @JsonProperty("model")
    private String model;

    @JsonProperty("choices")
    private List<GptModelResponseChoiceEntity> choices;

    @JsonProperty("usage")
    private GptModelResponseUsageEntity usage;

    @JsonProperty("error")
    private GptModelResponseErrorEntity error;
}
