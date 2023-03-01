package es.thalesalv.gptbot.adapters.discord.listener;

import java.text.MessageFormat;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.service.models.gpt.GptModel;
import es.thalesalv.gptbot.application.service.usecases.BotUseCase;
import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
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
    private final ApplicationContext applicationContext;

    private static final String MODEL = "Model";
    private static final String USE_CASE = "UseCase";
    private static final String MESSAGE_FLAGGED = "The message you sent has content that was flagged by OpenAI''s moderation. Your message has been deleted from the conversation channel. Message content: \n{0}";
    private static final String MESSAGE_EMPTY_RESPONSE = "The AI generated no output for your message. Your message has been deleted from the conversation channel. Please write a longer message and try again. Message content: \n{0}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageListener.class);

    @Override
    public void onMessageReceived(final @Nonnull MessageReceivedEvent event) {

        LOGGER.debug("Message received -> {}", event);
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
                        final GptModel model = (GptModel) applicationContext.getBean(persona.getModel() + MODEL);
                        final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                        useCase.generateResponse(bot, messageAuthor, message, channel, mentions, model);
                    }
                });
            }
        } catch (ModerationException e) {
            messageAuthor.openPrivateChannel()
                    .queue(privateChannel -> {
                        message.delete().queue();
                        privateChannel.sendMessage(MessageFormat.format(MESSAGE_FLAGGED, message.getContentDisplay())).queue();
                    });
        } catch (ErrorBotResponseException e) {
            channel.sendMessage("An error occured and the message could not be sent to the mode. Please try again.").queue();
        } catch (ModelResponseBlankException e) {
            LOGGER.error("The bot generated no text in response to the prompt -> {}", e);
            messageAuthor.openPrivateChannel()
                    .queue(privateChannel -> {
                        message.delete().queue();
                        privateChannel.sendMessage(MessageFormat.format(MESSAGE_EMPTY_RESPONSE, message.getContentDisplay())).queue();
                    });
        }catch (Exception e) {
            LOGGER.error("Unknown exception thrown -> {}", e);
        }
    }
}
