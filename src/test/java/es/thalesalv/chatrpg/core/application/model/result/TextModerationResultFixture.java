package es.thalesalv.chatrpg.core.application.model.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextModerationResultFixture {

    public static TextModerationResult.Builder withFlags() {

        List<String> flaggedTopics = new ArrayList<>();
        flaggedTopics.add("violence");

        Map<String, Double> flaggedTopicScores = new HashMap<>();
        flaggedTopicScores.put("violence", 10.0);

        return TextModerationResult.builder()
                .contentFlagged(true)
                .flaggedTopics(flaggedTopics)
                .moderationScores(flaggedTopicScores);
    }

    public static TextModerationResult.Builder withoutFlags() {

        List<String> flaggedTopics = new ArrayList<>();
        Map<String, Double> flaggedTopicScores = new HashMap<>();

        return TextModerationResult.builder()
                .contentFlagged(false)
                .flaggedTopics(flaggedTopics)
                .moderationScores(flaggedTopicScores);
    }
}
