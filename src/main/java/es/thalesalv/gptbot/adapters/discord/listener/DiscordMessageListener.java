package es.thalesalv.gptbot.adapters.discord.listener;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.service.models.gpt.GptModel;
import es.thalesalv.gptbot.application.service.usecases.BotUseCase;
import es.thalesalv.gptbot.application.translator.MessageEventDataTranslator;
import es.thalesalv.gptbot.domain.exception.ErrorBotResponseException;
import es.thalesalv.gptbot.domain.exception.ModelResponseBlankException;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@Service
@RequiredArgsConstructor
public class DiscordMessageListener extends ListenerAdapter {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final ApplicationContext applicationContext;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String MODEL = "Model";
    private static final String USE_CASE = "UseCase";
    private static final String MESSAGE_EMPTY_RESPONSE = "The AI generated no output for your message. Your message has been deleted from the conversation channel. Please write a longer message and try again. Message content: \n{0}";
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordMessageListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

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
                        contextDatastore.setMessageEventData(messageEventDataTranslator.translate(bot, messageAuthor, message, channel));
                        final GptModel model = (GptModel) applicationContext.getBean(persona.getModel() + MODEL);
                        final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                        useCase.generateResponse(bot, messageAuthor, message, channel, mentions, model);
                    }
                });
            }
        } catch (ErrorBotResponseException e) {
            channel.sendMessage("An error occured and the message could not be sent to the mode. Please try again.").complete();
        } catch (ModelResponseBlankException e) {
            LOGGER.error("The bot generated no text in response to the prompt", e);
            message.delete().complete();
            final PrivateChannel privateChannel = messageAuthor.openPrivateChannel().complete();
            privateChannel.sendMessage(MessageFormat.format(MESSAGE_EMPTY_RESPONSE, message.getContentDisplay())).complete();
        }catch (Exception e) {
            LOGGER.error("Unknown exception thrown", e);
        }
    }
}
