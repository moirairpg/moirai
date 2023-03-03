package es.thalesalv.gptbot.application.translator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;

@Component
public class GptRequestTranslator {

    @Value("${config.bot.generation.default-max-tokens}")
    private int defaultMaxTokens;

    @Value("${config.bot.generation.default-temperature}")
    private double defaultTemperature;

    @Value("${config.bot.generation.default-presence-penalty}")
    private double defaultPresencePenalty;

    @Value("${config.bot.generation.default-frequency-penalty}")
    private double defaultFrequencyPenalty;

    public GptRequest buildRequest(String prompt, String model, Persona persona) {

        final int maxTokens = persona == null ? defaultMaxTokens : persona.getMaxTokens();
        final double temperature = persona == null ? defaultTemperature : persona.getTemperature();
        final double presencePenalty = persona == null ? defaultPresencePenalty : persona.getPresencePenalty();
        final double frequencyPenalty = persona == null ? defaultFrequencyPenalty : persona.getFrequencyPenalty();

        return GptRequest.builder()
            .prompt(prompt)
            .model(model)
            .maxTokens(maxTokens)
            .temperature(temperature)
            .presencePenalty(presencePenalty)
            .frequencyPenalty(frequencyPenalty)
            .build();
    }
}
