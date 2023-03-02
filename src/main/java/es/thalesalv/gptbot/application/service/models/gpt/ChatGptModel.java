package es.thalesalv.gptbot.application.service.models.gpt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.gptbot.application.service.GptService;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChatGptModel implements GptModel {

    private final GptService gptService;

    private static final String MODEL_CHATGPT = "gpt-3.5-turbo";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatGptModel.class);

    @Override
    public Mono<String> generate(String prompt) {

        LOGGER.debug("Called inference with ChatGPT");
        return gptService.callChatCompletion(prompt, MODEL_CHATGPT).map(response -> {
            final String responseText = response.getChoices().get(0).getText();
            if (StringUtils.isBlank(responseText)) {
                throw new ModelResponseBlankException();
            }

            return responseText.trim();
        });
    }
}
