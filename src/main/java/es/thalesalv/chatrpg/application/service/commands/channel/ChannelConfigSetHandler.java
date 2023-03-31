package es.thalesalv.chatrpg.application.service.commands.channel;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
@Transactional
@RequiredArgsConstructor
public class ChannelConfigSetHandler {
    private static final String ID_OPTION = "id";
    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String ID_MISSING = "Configuration ID is required for attaching a a config to the current channel and cannot be empty.";
    private static final String CHANNEL_CONFIG_NOT_FOUND = "The requested channel configuration could not be found.";
    private static final String CHANNEL_LINKED_CONFIG = "Channel `{0}` was linked to configuration `{1}` (with persona `{2}`).";
    private static final String ERROR_SETTING_DEFINITION = "An error occurred while setting a definition";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when attaching config to channel. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelConfigSetHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for message edition");
            event.deferReply();
            final String id = Optional.ofNullable(event.getOption(ID_OPTION))
                    .map(OptionMapping::getAsString)
                    .orElseThrow(() -> new IllegalArgumentException(ID_MISSING));

            channelConfigRepository.findById(id)
                    .map(config -> {
                        final ChannelEntity entity = channelRepository.findByChannelId(event.getChannel()
                                .getId())
                                .map(e -> {
                                    e.setChannelConfig(config);
                                    e.setChannelId(event.getChannel()
                                            .getId());
                                    return e;
                                })
                                .orElseGet(() -> ChannelEntity.builder()
                                        .channelConfig(config)
                                        .channelId(event.getChannel()
                                                .getId())
                                        .build());

                        channelRepository.save(entity);
                        event.reply(MessageFormat.format(CHANNEL_LINKED_CONFIG, event.getChannel()
                                .getName(), entity.getId(),
                                config.getPersona()
                                        .getName()))
                                .setEphemeral(true)
                                .queue(reply -> reply.deleteOriginal()
                                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        return entity;
                    })
                    .orElseThrow(() -> new ChannelConfigurationNotFoundException(CHANNEL_CONFIG_NOT_FOUND));
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_DEFINITION, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}
