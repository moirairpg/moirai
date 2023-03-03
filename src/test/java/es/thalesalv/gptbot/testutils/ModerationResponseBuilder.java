package es.thalesalv.gptbot.testutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResponse;
import es.thalesalv.gptbot.domain.model.openai.moderation.ModerationResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModerationResponseBuilder {

    public static ModerationResponse moderationResponse() {

        List<ModerationResult> moderationResults = new ArrayList<>();
        moderationResults.add(moderationResult());

        return ModerationResponse.builder()
                .id("534545")
                .model("text-davinci-003")
                .moderationResult(moderationResults)
                .build();
    }

    public static ModerationResult moderationResult() {

        final Map<String, Boolean> categories = new HashMap<>();
        categories.put("hate", false);
        categories.put("hate/threatening", false);
        categories.put("self/harm", false);
        categories.put("sexual", false);
        categories.put("sexual/minors", false);
        categories.put("violence", false);
        categories.put("violence/graphic", false);

        final Map<String, Double> scores = new HashMap<>();
        scores.put("hate", 0.0);
        scores.put("hate/threatening", 0.0);
        scores.put("self/harm", 0.0);
        scores.put("sexual", 0.0);
        scores.put("sexual/minors", 0.0);
        scores.put("violence", 0.0);
        scores.put("violence/graphic", 0.0);

        return ModerationResult.builder()
                .flagged(false)
                .categories(categories)
                .categoryScores(scores)
                .build();
    }

    public static ModerationResult moderationResultSexualFlag() {

        final Map<String, Boolean> categories = new HashMap<>();
        categories.put("hate", false);
        categories.put("hate/threatening", false);
        categories.put("self/harm", false);
        categories.put("sexual", true);
        categories.put("sexual/minors", false);
        categories.put("violence", false);
        categories.put("violence/graphic", false);

        final Map<String, Double> scores = new HashMap<>();
        scores.put("hate", 0.0);
        scores.put("hate/threatening", 0.0);
        scores.put("self/harm", 0.0);
        scores.put("sexual", 10.0);
        scores.put("sexual/minors", 0.0);
        scores.put("violence", 0.0);
        scores.put("violence/graphic", 0.0);

        return ModerationResult.builder()
                .flagged(true)
                .categories(categories)
                .categoryScores(scores)
                .build();
    }
}
