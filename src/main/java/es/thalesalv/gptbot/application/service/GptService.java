package es.thalesalv.gptbot.application.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.thalesalv.gptbot.adapters.rest.OpenAIApiService;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.translator.GptRequestTranslator;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptRequest;
import es.thalesalv.gptbot.domain.model.openai.gpt.GptResponse;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final ObjectMapper objectMapper;
    private final GptRequestTranslator gptRequestTranslator;
    private final OpenAIApiService openAiService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<GptResponse> callCompletion(final String prompt, final String model, final Persona persona) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, model, persona);
        return openAiService.callGptApi(request);
    }

    public Mono<GptResponse> callChatCompletion(final String prompt, final String model, final Persona persona) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        final GptRequest request = gptRequestTranslator.buildRequest(prompt, model, persona);
        return openAiService.callGptChatApi(request);
    }
}
