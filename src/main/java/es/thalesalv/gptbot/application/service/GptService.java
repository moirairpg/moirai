package es.thalesalv.gptbot.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final GptRequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<GptResponse> callCompletion(final String prompt, final String model) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, model);
        return openAiService.callGptApi(request);
    }

    public Mono<GptResponse> callChatCompletion(final String prompt, final String model) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, model);
        return openAiService.callGptChatApi(request);
    }
}
