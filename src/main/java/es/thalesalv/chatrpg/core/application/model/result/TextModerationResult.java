package es.thalesalv.chatrpg.core.application.model.result;

import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(builderClassName = "Builder")
public class TextModerationResult {

    private boolean hasFlaggedContent;
    private Map<String, Double> moderationScores;
}
