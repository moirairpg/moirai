package es.thalesalv.gptbot.testutils;

import java.util.HashMap;
import java.util.Map;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonaBuilder {

    public static Persona persona() {

        final Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("hate", 0.0);
        thresholds.put("hate/threatening", 0.0);
        thresholds.put("self/harm", 0.0);
        thresholds.put("sexual", 0.0);
        thresholds.put("sexual/minors", 0.0);
        thresholds.put("violence", 0.0);
        thresholds.put("violence/graphic", 0.0);

        return Persona.builder()
                .maxTokens(100)
                .intent("rpg")
                .modelName("text-davinci-003")
                .modelFamily("gpt3")
                .moderationAbsolute("false")
                .moderationRules(thresholds)
                .personality("I am a robot")
                .build();
    }
    
    public static Persona personaAbsoluteModeration() {

        final Map<String, Double> thresholds = new HashMap<>();
        thresholds.put("hate", 0.0);
        thresholds.put("hate/threatening", 0.0);
        thresholds.put("self/harm", 0.0);
        thresholds.put("sexual", 0.0);
        thresholds.put("sexual/minors", 0.0);
        thresholds.put("violence", 0.0);
        thresholds.put("violence/graphic", 0.0);

        return Persona.builder()
                .maxTokens(100)
                .intent("rpg")
                .modelName("text-davinci-003")
                .modelFamily("gpt3")
                .moderationAbsolute("true")
                .moderationRules(thresholds)
                .personality("I am a robot")
                .build();
    }

    public static MessageEventData messageEventData() {

        return MessageEventData.builder()
                .botId("4234235")
                .channelId("4235235")
                .messageAuthorId("4234234")
                .messageId("45345345")
                .build();
    }
}
