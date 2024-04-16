package es.thalesalv.chatrpg.infrastructure.outbound.adapter.response;

import java.util.Map;

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
public class ModerationResult {

    @JsonProperty("categories")
    private Map<String, Boolean> categories;

    @JsonProperty("category_scores")
    private Map<String, String> categoryScores;

    @JsonProperty("flagged")
    private Boolean flagged;
}