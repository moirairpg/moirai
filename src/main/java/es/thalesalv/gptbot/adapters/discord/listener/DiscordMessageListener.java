package es.thalesalv.gptbot.adapters.discord.listener;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.usecases.BotUseCase;
import es.thalesalv.gptbot.domain.exception.ModerationException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final BeanFactory factory;

    private static final String USE_CASE = "UseCase";
    private static final String MESSAGE_FLAGGED = "The message you sent has content that was flagged by OpenAI''s moderation. Message content: \n{0}";

    @Override
    public void onMessageReceived(final @Nonnull MessageReceivedEvent event) {

        final SelfUser bot = event.getJDA().getSelfUser();
        final Message message = event.getMessage();
        final MessageChannelUnion channel = event.getChannel();
        final User messageAuthor = event.getAuthor();
        final Mentions mentions = message.getMentions();

        try {
            if (!messageAuthor.isBot()) {
                botConfig.getPersonas().forEach(persona -> {
                    final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id));
                    if (isCurrentChannel) {
                        contextDatastore.setPersona(persona);
                        final BotUseCase useCase = (BotUseCase) factory.getBean(persona.getIntent() + USE_CASE);
                        useCase.generateResponse(bot, messageAuthor, message, channel, mentions);
                    }
                });
            }
        } catch (ModerationException e) {
            messageAuthor.openPrivateChannel()
                    .queue(privateChannel -> {
                        message.delete().queue();
                        privateChannel.sendMessage(MessageFormat.format(MESSAGE_FLAGGED, message.getContentDisplay()))
                                .queue();
                    });
        }
    }
}
