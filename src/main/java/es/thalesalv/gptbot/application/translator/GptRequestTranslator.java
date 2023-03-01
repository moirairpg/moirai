package es.thalesalv.gptbot.application.translator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GptRequestTranslator {

    @Value("${config.bot.generation.default-max-tokens}")
    private int defaultMaxTokens;

    @Value("${config.bot.generation.default-temperature}")
    private double defaultTemperature;

    @Value("${config.bot.generation.default-presence-penalty}")
    private double defaultPresencePenalty;

    @Value("${config.bot.generation.default-frequency-penalty}")
    private double defaultFrequencyPenalty;

    private final ContextDatastore contextDatastore;

    public GptRequest buildRequest(String prompt, String model) {

        final int maxTokens = contextDatastore.isPersonaNull() ? defaultMaxTokens : contextDatastore.getPersona().getMaxTokens();
        final double temperature = contextDatastore.isPersonaNull() ? defaultTemperature : contextDatastore.getPersona().getTemperature();
        final double presencePenalty = contextDatastore.isPersonaNull() ? defaultPresencePenalty : contextDatastore.getPersona().getPresencePenalty();
        final double frequencyPenalty = contextDatastore.isPersonaNull() ? defaultFrequencyPenalty : contextDatastore.getPersona().getFrequencyPenalty();

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
