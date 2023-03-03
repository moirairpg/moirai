package es.thalesalv.gptbot.application.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.interfaces.GptModel;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatGptModel implements GptModel {

    private final GptRequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGptModel.class);

    @Override
    public Mono<String> generate(final String prompt, final Persona persona) {

        LOGGER.debug("Called inference for ChatGPT. Model name -> {}", persona.getModelName());
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, persona.getModelName(), persona);
        return openAiService.callGptChatApi(request).map(response -> {
            final String responseText = response.getChoices().get(0).getText();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        });
    }
}
