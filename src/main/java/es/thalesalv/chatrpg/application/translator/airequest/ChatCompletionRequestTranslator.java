package es.thalesalv.chatrpg.application.translator.airequest;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.enums.AIModelEnum;
import es.thalesalv.chatrpg.domain.model.openai.dto.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatCompletionRequestTranslator {

    public ChatGptRequest buildRequest(final List<ChatGptMessage> chatGptMessages, final ChannelConfig channelConfig) {

        final ModelSettings modelSettings = channelConfig.getSettings().getModelSettings();
        final String modelName = AIModelEnum.findByInternalName(modelSettings.getModelName()).getModelName();
        return ChatGptRequest.builder()
            .messages(chatGptMessages)
            .model(modelName)
            .maxTokens(modelSettings.getMaxTokens())
            .temperature(modelSettings.getTemperature())
            .presencePenalty(modelSettings.getPresencePenalty())
            .frequencyPenalty(modelSettings.getFrequencyPenalty())
            .logitBias(modelSettings.getLogitBias())
            .build();
    }
}
