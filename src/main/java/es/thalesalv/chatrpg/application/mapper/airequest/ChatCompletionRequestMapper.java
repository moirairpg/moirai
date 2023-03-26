package es.thalesalv.chatrpg.application.mapper.airequest;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatCompletionRequest;
import es.thalesalv.chatrpg.domain.model.openai.completion.ChatMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatCompletionRequestMapper {

    public ChatCompletionRequest buildRequest(final List<ChatMessage> chatMessages, final ChannelConfig channelConfig) {

        final ModelSettings modelSettings = channelConfig.getSettings().getModelSettings();
        final String modelName = AIModelEnum.findByInternalName(modelSettings.getModelName()).getModelName();
        return ChatCompletionRequest.builder()
            .messages(chatMessages)
            .model(modelName)
            .maxTokens(modelSettings.getMaxTokens())
            .temperature(modelSettings.getTemperature())
            .presencePenalty(modelSettings.getPresencePenalty())
            .frequencyPenalty(modelSettings.getFrequencyPenalty())
            .logitBias(modelSettings.getLogitBias())
            .build();
    }
}
