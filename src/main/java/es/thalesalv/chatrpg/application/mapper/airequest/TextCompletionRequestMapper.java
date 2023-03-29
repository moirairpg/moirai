package es.thalesalv.chatrpg.application.mapper.airequest;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.completion.TextCompletionRequest;

@Component
public class TextCompletionRequestMapper {

    public TextCompletionRequest buildRequest(final String prompt, final ChannelConfig channelConfig) {

        final ModelSettings modelSettings = channelConfig.getSettings()
                .getModelSettings();
        final String modelName = AIModel.findByInternalName(modelSettings.getModelName())
                .getModelName();
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
