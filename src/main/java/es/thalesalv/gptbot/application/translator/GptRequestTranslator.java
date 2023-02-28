package es.thalesalv.gptbot.application.translator;

import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GptRequestTranslator {
    
    private final ContextDatastore contextDatastore;

    public GptRequest buildRequest(String prompt, String model) {

        final int maxTokens = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getMaxTokens() : 100;
        final double temperature = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getTemperature() : 0.6;
        final double presencePenalty = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getPresencePenalty() : 0.2;
        final double frequencyPenalty = contextDatastore.isCurrentChannel() ? contextDatastore.getCurrentChannel().getFrequencyPenalty() : 0.2;
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
