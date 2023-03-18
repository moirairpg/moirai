package es.thalesalv.chatrpg.application.translator;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelConfig;
import es.thalesalv.chatrpg.adapters.data.db.entity.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatGptRequestTranslator {

    public ChatGptRequest buildRequest(final List<ChatGptMessage> chatGptMessages, final ChannelConfig channelConfig) {

        final ModelSettings modelSettings = channelConfig.getModelSettings();
        return ChatGptRequest.builder()
            .messages(chatGptMessages)
            .model(modelSettings.getModelName())
            .maxTokens(modelSettings.getMaxTokens())
            .temperature(modelSettings.getTemperature())
            .presencePenalty(modelSettings.getPresencePenalty())
            .frequencyPenalty(modelSettings.getFrequencyPenalty())
            .logitBias(modelSettings.getLogitBias())
            .build();
    }
}
