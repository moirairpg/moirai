package es.thalesalv.chatrpg.application.translator;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.application.config.Persona;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptMessage;
import es.thalesalv.chatrpg.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatGptRequestTranslator {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());
    public ChatGptRequest buildRequest(final Persona persona, final List<ChatGptMessage> chatGptMessages) {
        LOG.info(persona.toString());
        return ChatGptRequest.builder()
            .messages(chatGptMessages)
            .model(persona.getModelName())
            .maxTokens(persona.getMaxTokens())
            .temperature(persona.getTemperature())
            .presencePenalty(persona.getPresencePenalty())
            .frequencyPenalty(persona.getFrequencyPenalty())
            .logitBias(persona.getBias())
            .build();
    }
}
