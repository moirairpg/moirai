package es.thalesalv.gptbot.application.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final GptRequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final String MODEL_CURIE = "text-curie-001";
    private static final String MODEL_BABBAGE = "text-babbage-001";
    private static final String MODEL_ADA = "text-ada-001";
    private static final String MODEL_DAVINCI = "text-davinci-003";
    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<GptResponse> callModel(final String prompt, final String model) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, model);
        return openAiService.callGptApi(request);
    }

    public Mono<String> callDaVinci(final String prompt) {

        LOGGER.debug("Called inference with Davinci");
        return callModel(prompt, MODEL_DAVINCI).map(response -> {
            final String responseText = response.getChoices().get(0).getText();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        });
    }

    public Mono<GptResponse> callAda(final String prompt) {

        LOGGER.debug("Called inference with Ada");
        return callModel(prompt, MODEL_ADA);
    }

    public Mono<GptResponse> callCurie(final String prompt) {

        LOGGER.debug("Called inference with Curie");
        return callModel(prompt, MODEL_CURIE);
    }

    public Mono<GptResponse> callBabbage(final String prompt) {

        LOGGER.debug("Called inference with Babbage");
        return callModel(prompt, MODEL_BABBAGE);
    }
}
