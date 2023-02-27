package es.thalesalv.gptbot.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.domain.model.gpt.GptResponseEntity;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GptService {

    private final OpenAIApiService openAiService;

    private static final String MODEL_ADA = "text-ada-001";
    private static final String MODEL_DAVINCI = "text-davinci-003";
    private static final Logger LOGGER = LoggerFactory.getLogger(GptService.class);

    public Mono<GptResponseEntity> callModel(final String prompt, final String model) {

        LOGGER.debug("Sending prompt to model. Model -> {}, Prompt -> {}", model, prompt);
        return openAiService.callGptApi(prompt, model);
    }

    public Mono<GptResponseEntity> callDaVinci(final String prompt) {

        return callModel(prompt, MODEL_DAVINCI);
    }

    public Mono<GptResponseEntity> callAda(final String prompt) {

        return callModel(prompt, MODEL_ADA);
    }
}
