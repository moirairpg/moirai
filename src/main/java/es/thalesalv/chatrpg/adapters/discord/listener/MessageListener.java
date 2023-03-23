package es.thalesalv.chatrpg.adapters.discord.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.mapper.chconfig.EventDataMapper;
import es.thalesalv.chatrpg.application.service.completion.CompletionService;
import es.thalesalv.chatrpg.application.service.usecases.BotUseCase;
import es.thalesalv.chatrpg.domain.enums.AIModel;
import es.thalesalv.chatrpg.domain.model.openai.dto.EventData;
import es.thalesalv.chatrpg.domain.model.openai.dto.ModelSettings;
import es.thalesalv.chatrpg.domain.model.openai.dto.Persona;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Service
@RequiredArgsConstructor
public class MessageListener {

    private final ChannelEntityToDTO channelEntityMapper;
    private final ChannelRepository channelRepository;
    private final ApplicationContext applicationContext;
    private final EventDataMapper eventDataTranslator;

    private static final String USE_CASE = "UseCase";
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageListener.class);

    public void onMessageReceived(MessageReceivedEvent event) {

        if (!event.getAuthor().isBot()) {
            channelRepository.findByChannelId(event.getChannel().getId())
                    .map(channelEntityMapper)
                    .ifPresent(channel -> {
                        LOGGER.debug("Received message by {} in {}: {}", event.getAuthor(), event.getChannel().getName(), event.getMessage().getContentDisplay());
                        final Persona persona = channel.getChannelConfig().getPersona();
                        final ModelSettings modelSettings = channel.getChannelConfig().getSettings().getModelSettings();
                        final String completionType = AIModel.findByInternalName(modelSettings.getModelName()).getCompletionType();
                        final EventData eventData = eventDataTranslator.translate(event, channel);
                        final CompletionService model = (CompletionService) applicationContext.getBean(completionType);
                        final BotUseCase useCase = (BotUseCase) applicationContext.getBean(persona.getIntent() + USE_CASE);
                        useCase.generateResponse(eventData, model);
                    });
        }
    }
}
