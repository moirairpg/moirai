package es.thalesalv.chatrpg.domain.model.openai.moderation;

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
public class ResultCategory {

    @JsonProperty("hate")
    private Boolean hate;

    @JsonProperty("hate/threatening")
    private Boolean hateThreatening;

    @JsonProperty("self-harm")
    private Boolean selfHarm;

    @JsonProperty("sexual")
    private Boolean sexual;

    @JsonProperty("sexual/minors")
    private Boolean sexualMinors;

    @JsonProperty("violence")
    private Boolean violence;

    @JsonProperty("violence/graphic")
    private Boolean violenceGraphic;
}
