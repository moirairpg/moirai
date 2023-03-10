package es.thalesalv.chatrpg.application.translator;

import java.util.List;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatGptRequestTranslator {

    public ChatGptRequest buildRequest(final List<String> messages, final Persona persona, final List<ChatGptMessage> chatGptMessages) {

        return ChatGptRequest.builder()
            .messages(chatGptMessages)
            .model(persona.getModelName())
            .maxTokens(persona.getMaxTokens())
            .temperature(persona.getTemperature())
            .presencePenalty(persona.getPresencePenalty())
            .frequencyPenalty(persona.getFrequencyPenalty())
            .build();
    }
}
