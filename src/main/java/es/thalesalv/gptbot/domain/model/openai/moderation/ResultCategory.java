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
public class ResultCategory {

    @JsonProperty("hate")
    private boolean hate;

    @JsonProperty("hate/threatening")
    private boolean hateThreatening;

    @JsonProperty("self-harm")
    private boolean selfHarm;

    @JsonProperty("sexual")
    private boolean sexual;

    @JsonProperty("sexual/minors")
    private boolean sexualMinors;

    @JsonProperty("violence")
    private boolean violence;

    @JsonProperty("violence/graphic")
    private boolean violenceGraphic;
}
