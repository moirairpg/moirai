package es.thalesalv.gptbot.application.service.usecases;

import es.thalesalv.gptbot.application.config.MessageEventData;
import es.thalesalv.gptbot.application.config.Persona;
import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import net.dv8tion.jda.api.entities.Mentions;

@FunctionalInterface
public interface BotUseCase {

    /**
     * Formats the prompt so it can be sent to the AI based on specific logic
     * @param persona Object containing the current persona with bot behavior
     * @param messageEventData Object containing data on the message event
     * @param mentions List of Discord users mentioned in the conversation
     * @param model Model to be used for generation
     */
    void generateResponse(final Persona persona, final MessageEventData messageEventData, final Mentions mentions, final GptModelService model);
}
