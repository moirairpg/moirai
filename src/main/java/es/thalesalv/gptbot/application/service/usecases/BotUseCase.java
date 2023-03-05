package es.thalesalv.gptbot.application.service.usecases;

import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@FunctionalInterface
public interface BotUseCase {

    /**
     * Formats the prompt so it can be sent to the AI based on specific logic
     * @param bot Bot user
     * @param messageAuthor Author of the last message sent to the channel
     * @param message Last message sent to the channel
     * @param channel Channel where the conversation is happening
     * @param mentions List of Discord users mentioned in the conversation
     * @param model Model to be used for generation
     */
    void generateResponse(final SelfUser bot, final User messageAuthor, final Message message, final MessageChannelUnion channel, final Mentions mentions, final GptModelService model);
}
