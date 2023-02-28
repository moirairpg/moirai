package es.thalesalv.gptbot.domain.model.openai.gpt;

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
public class GptRequest {

    @JsonProperty("model")
    private String model;

    @JsonProperty("prompt")
    private String prompt;

    @JsonProperty("stop")
    private String stop;

    @JsonProperty("max_tokens")
    private int maxTokens;

//    @JsonProperty("n")
//    private int n;

    @JsonProperty("temperature")
    private double temperature;

//    @JsonProperty("top_p")
//    private double topP;

   @JsonProperty("presence_penalty")
   private double presencePenalty;

   @JsonProperty("frequency_penalty")
   private double frequencyPenalty;
}
