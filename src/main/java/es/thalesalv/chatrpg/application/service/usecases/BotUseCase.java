package es.thalesalv.chatrpg.application.service.usecases;

import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.domain.model.EventData;

public interface BotUseCase {

    /**
     * Formats the prompt so it can be sent to the AI based on specific logic
     *
     * @param eventData Object containing data on the message event
     * @param model     Model to be used for generation
     */
    void generateResponse(final EventData eventData, final CompletionService model);
}
