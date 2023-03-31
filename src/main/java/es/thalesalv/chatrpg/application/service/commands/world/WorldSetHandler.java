package es.thalesalv.chatrpg.application.service.commands.world;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.entity.ChannelEntity;
import es.thalesalv.chatrpg.adapters.data.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.repository.WorldRepository;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Component
@Transactional
@RequiredArgsConstructor
public class WorldSetHandler {

    private final WorldRepository worldRepository;
    private final ChannelRepository channelRepository;

    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String CHANNEL_CONFIG_NOT_FOUND = "The current channel does not have a configuration attached to it.";
    private static final String ID_MISSING = "World ID is required for attaching a world to the current config and cannot be empty.";
    private static final String ERROR_SETTING_DEFINITION = "An error occurred while setting a definition";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when attaching world to config. Please try again.";
    private static final String WORLD_ID_NOT_FOUND = "The world with the requested ID does not exist.";
    private static final String WORLD_LINKED_CHANNEL_CONFIG = "World `{0}` was linked to configuration to the configuration of channel `{1}` (with persona `{2}`)";

    private static final Logger LOGGER = LoggerFactory.getLogger(WorldSetHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for message edition");
            event.deferReply();

            final String id = Optional.ofNullable(event.getOption("id"))
                    .map(OptionMapping::getAsString)
                    .orElseThrow(() -> new IllegalArgumentException(ID_MISSING));

            worldRepository.findById(id)
                    .map(world -> channelRepository.findByChannelId(event.getChannel()
                            .getId())
                            .map(channel -> attachWorldToConfig(channel, world, event))
                            .orElseThrow(() -> new ChannelConfigurationNotFoundException(CHANNEL_CONFIG_NOT_FOUND)))
                    .orElseThrow(() -> new WorldNotFoundException(WORLD_ID_NOT_FOUND));
        } catch (ChannelConfigurationNotFoundException | WorldNotFoundException e) {
            LOGGER.debug(e.getMessage(), e);
            event.reply(e.getMessage())
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_DEFINITION, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }

    private ChannelEntity attachWorldToConfig(final ChannelEntity channel, final WorldEntity world,
            final SlashCommandInteractionEvent event) {

        LOGGER.debug("World {} will be attached to channel {} (channel config ID {})", world.getName(),
                event.getChannel()
                        .getName(),
                channel.getId());
        channel.getChannelConfig()
                .setWorld(world);
        channelRepository.save(channel);
        event.reply(MessageFormat.format(WORLD_LINKED_CHANNEL_CONFIG, world.getName(), channel.getId(),
                channel.getChannelConfig()
                        .getPersona()
                        .getName()))
                .setEphemeral(true)
                .queue(reply -> reply.deleteOriginal()
                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        return channel;
    }
}
