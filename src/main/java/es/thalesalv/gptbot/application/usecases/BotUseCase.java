package es.thalesalv.gptbot.application.usecases;

import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

public interface BotUseCase {

    void generateResponse(final SelfUser bot, final User messageAuthor, final Message message, final MessageChannelUnion channel, final Mentions mentions);
}
