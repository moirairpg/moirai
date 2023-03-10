package es.thalesalv.chatrpg.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.Gpt3Request;

@Component
public class Gpt3RequestTranslator {

    public Gpt3Request buildRequest(final String prompt, final Persona persona) {

        final String formattedPrompt = persona.getPersonality() + "\n" + prompt;

        return Gpt3Request.builder()
            .prompt(formattedPrompt)
            .model(persona.getModelName())
            .maxTokens(persona.getMaxTokens())
            .temperature(persona.getTemperature())
            .presencePenalty(persona.getPresencePenalty())
            .frequencyPenalty(persona.getFrequencyPenalty())
            .build();
    }
}
