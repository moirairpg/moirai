package es.thalesalv.chatrpg.application.translator.airequest;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.enums.AIModelEnum;
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;

@Component
public class TextCompletionRequestTranslator {

    public TextCompletionRequest buildRequest(final String prompt, final ChannelConfig channelConfig) {

        final ModelSettings modelSettings = channelConfig.getSettings().getModelSettings();
        final String modelName = AIModelEnum.findByInternalName(modelSettings.getModelName()).getModelName();

        return TextCompletionRequest.builder()
            .prompt(prompt)
            .model(modelName)
            .maxTokens(modelSettings.getMaxTokens())
            .temperature(modelSettings.getTemperature())
            .presencePenalty(modelSettings.getPresencePenalty())
            .frequencyPenalty(modelSettings.getFrequencyPenalty())
            .logitBias(modelSettings.getLogitBias())
            .build();
    }
}
