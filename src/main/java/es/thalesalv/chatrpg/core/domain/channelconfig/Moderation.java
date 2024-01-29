package es.thalesalv.chatrpg.core.domain.channelconfig;

import java.util.Map;
import java.util.HashMap;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Moderation {

    STRICT(true, null),
    PERMISSIVE(false, permissiveThresholds()),
    DISABLED(false, null);

    private boolean isAbsolute;
    private Map<String, Double> thresholds;

    private static final String HATE = "hate";
    private static final String HATE_THREATENING = "hate/threatening";
    private static final String HARASSMENT = "harassment";
    private static final String HARASSMENT_THREATENING = "harassment/threatening";
    private static final String SELF_HARM = "self-harm";
    private static final String SELF_HARM_INTENT = "self-harm/intent";
    private static final String SELF_HARM_INSTRUCTIONS = "self-harm/instructions";
    private static final String SEXUAL = "sexual";
    private static final String SEXUAL_MINORS = "sexual/minors";
    private static final String VIOLENCE = "violence";
    private static final String VIOLENCE_GRAPHIC = "violence/graphic";

    private static Map<String, Double> permissiveThresholds() {

        Map<String, Double> thresholds = new HashMap<>();
        thresholds.put(HATE, 0.7);
        thresholds.put(HATE_THREATENING, 0.6);
        thresholds.put(HARASSMENT, 0.6);
        thresholds.put(HARASSMENT_THREATENING, 0.6);
        thresholds.put(SELF_HARM, 0.3);
        thresholds.put(SELF_HARM_INTENT, 0.3);
        thresholds.put(SELF_HARM_INSTRUCTIONS, 0.3);
        thresholds.put(SEXUAL, 0.5);
        thresholds.put(SEXUAL_MINORS, 0.1);
        thresholds.put(VIOLENCE, 1.1);
        thresholds.put(VIOLENCE_GRAPHIC, 0.4);

        return thresholds;
    }
}
