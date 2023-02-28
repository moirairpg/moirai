package es.thalesalv.gptbot.domain.model.openai.moderation;

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
public class ResultCategoryScores {

    @JsonProperty("hate")
    private double hate;

    @JsonProperty("hate/threatening")
    private double hateThreatening;

    @JsonProperty("self-harm")
    private double selfHarm;

    @JsonProperty("sexual")
    private double sexual;

    @JsonProperty("sexual/minors")
    private double sexualMinors;

    @JsonProperty("violence")
    private double violence;

    @JsonProperty("violence/graphic")
    private double violenceGraphic;
}
