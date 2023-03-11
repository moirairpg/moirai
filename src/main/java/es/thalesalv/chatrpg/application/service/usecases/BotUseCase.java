package es.thalesalv.chatrpg.application.service.usecases;

import es.thalesalv.chatrpg.application.config.MessageEventData;
import es.thalesalv.chatrpg.application.service.interfaces.GptModelService;

@FunctionalInterface
public interface BotUseCase {

    /**
     * Formats the prompt so it can be sent to the AI based on specific logic
     * @param messageEventData Object containing data on the message event
     * @param model Model to be used for generation
     */
    void generateResponse(final MessageEventData messageEventData, final GptModelService model);
}
