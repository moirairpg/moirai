package es.thalesalv.chatrpg.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.service.GptModelService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.application.translator.ChannelEntityListToDTOList;
import es.thalesalv.chatrpg.application.translator.MessageEventDataTranslator;
import es.thalesalv.chatrpg.domain.model.openai.dto.MessageEventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
@RequiredArgsConstructor
public class MessageListener {

    private final ChannelEntityListToDTOList channelEntityListToDTOList;
    private final ChannelRepository channelRepository;
    private final ApplicationContext applicationContext;
    private final MessageEventDataTranslator messageEventDataTranslator;

    private static final String MODEL_SERVICE = "ModelService";
    private static final String USE_CASE = "UseCase";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            channelEntityListToDTOList.apply(channelRepository.findAll()).stream()
                .filter(c -> c.getChannelId().equals(event.getChannel().getId()))
                .findFirst()
                .ifPresent(channel -> {
                    LOGGER.debug("Received message by {} in {}: {}", event.getAuthor(), event.getChannel().getName(), event.getMessage().getContentDisplay());
                    final Persona persona = channel.getChannelConfig().getPersona();
                    final ModelSettings modelSettings = channel.getChannelConfig().getSettings().getModelSettings();
                	final MessageEventData messageEventData = messageEventDataTranslator.translate(event, channel.getChannelConfig());
                    final GptModelService model = (GptModelService) applicationContext.getBean(modelSettings.getModelFamily() + MODEL_SERVICE);
                    final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                    useCase.generateResponse(messageEventData, model);
                });
        }
    }
}
