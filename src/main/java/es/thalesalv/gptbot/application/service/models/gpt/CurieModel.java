package es.thalesalv.gptbot.application.service.models.gpt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.GptService;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class CurieModel implements GptModel {

    private final GptService gptService;

    private static final String MODEL_CURIE = "text-curie-001";
    private static final Logger LOGGER = LoggerFactory.getLogger(CurieModel.class);

    @Override
    public Mono<String> generate(final String prompt, final Persona persona) {

        LOGGER.debug("Called inference with Curie");
        return gptService.callCompletion(prompt, MODEL_CURIE, persona).map(response -> {
            final String responseText = response.getChoices().get(0).getText();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        });
    }
}
