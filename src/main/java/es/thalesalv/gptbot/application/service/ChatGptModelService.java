package es.thalesalv.gptbot.application.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.errorhandling.CommonErrorHandler;
import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import es.thalesalv.gptbot.application.translator.ChatGptRequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.ChatGptRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatGptModelService implements GptModelService {

    private final ContextDatastore contextDatastore;
    private final CommonErrorHandler commonErrorHandler;
    private final ChatGptRequestTranslator chatGptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGptModelService.class);

    @Override
    public Mono<String> generate(final String prompt, final Persona persona, final List<String> messages) {

        LOGGER.debug("Called inference for ChatGPT. Persona -> {}", persona);
        final MessageEventData messageEventData = contextDatastore.getMessageEventData();
        final ChatGptRequest request = chatGptRequestTranslator.buildRequest(messages, persona.getModelName(), persona);
        return openAiService.callGptChatApi(request).map(response -> {
            final String responseText = response.getChoices().get(0).getMessage().getContent();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        })
        .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(messageEventData));
    }
}
