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
import es.thalesalv.gptbot.application.translator.Gpt3RequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.Gpt3Request;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Gpt3ModelService implements GptModelService {

    private final ContextDatastore contextDatastore;
    private final CommonErrorHandler commonErrorHandler;
    private final Gpt3RequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(Gpt3ModelService.class);

    @Override
    public Mono<String> generate(final String prompt, final Persona persona, final List<String> messages) {

        LOGGER.debug("Called inference for GPT3. Persona -> {}", persona);
        final MessageEventData messageEventData = contextDatastore.getMessageEventData();
        final Gpt3Request request = gptRequestTranslator.buildRequest(prompt, persona.getModelName(), persona);
        return openAiService.callGptApi(request).map(response -> {
            final String responseText = response.getChoices().get(0).getText();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        })
        .doOnError(ModelResponseBlankException.class::isInstance, e -> commonErrorHandler.handleEmptyResponse(messageEventData));
    }
}