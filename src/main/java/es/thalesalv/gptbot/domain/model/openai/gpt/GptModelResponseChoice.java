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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GptModelResponseChoice {

    @JsonProperty("text")
    private String text;

    @JsonProperty("finish_reason")
    private String finishReason;

    @JsonProperty("index")
    private int index;
}
