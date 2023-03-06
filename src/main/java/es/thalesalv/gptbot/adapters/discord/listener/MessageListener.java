package es.thalesalv.gptbot.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.gptbot.adapters.data.ContextDatastore;
import es.thalesalv.gptbot.application.config.BotConfig;
import es.thalesalv.gptbot.application.service.interfaces.GptModelService;
import es.thalesalv.gptbot.application.service.usecases.BotUseCase;
import es.thalesalv.gptbot.application.translator.MessageEventDataTranslator;
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
public class MessageListener extends ListenerAdapter {

    private final BotConfig botConfig;
    private final ContextDatastore contextDatastore;
    private final ApplicationContext applicationContext;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String MODEL_SERVICE = "ModelService";
    private static final String USE_CASE = "UseCase";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        LOGGER.debug("Message received -> {}", event);
        final SelfUser bot = event.getJDA().getSelfUser();
        final Message message = event.getMessage();
        final MessageChannelUnion channel = event.getChannel();
        final User messageAuthor = event.getAuthor();
        final Mentions mentions = message.getMentions();

        if (!messageAuthor.isBot()) {
            botConfig.getPersonas().forEach(persona -> {
                final boolean isCurrentChannel = persona.getChannelIds().stream().anyMatch(id -> channel.getId().equals(id));
                if (isCurrentChannel) {
                    contextDatastore.setPersona(persona);
                    contextDatastore.setMessageEventData(messageEventDataTranslator.translate(bot, messageAuthor, message, channel));
                    final GptModelService model = (GptModelService) applicationContext.getBean(persona.getModelFamily() + MODEL_SERVICE);
                    final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                    useCase.generateResponse(bot, messageAuthor, message, channel, mentions, model);
                }
            });
        }
    }
}
