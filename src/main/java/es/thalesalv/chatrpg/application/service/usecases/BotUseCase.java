package es.thalesalv.chatrpg.application.service.usecases;

import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;

public interface BotUseCase {

    /**
     * Formats the prompt so it can be sent to the AI based on specific logic
     * @param messageEventData Object containing data on the message event
     * @param model Model to be used for generation
     * @return messageEventData together with the bot's response
     */
    MessageEventData generateResponse(final MessageEventData messageEventData, final CompletionService model);
}
