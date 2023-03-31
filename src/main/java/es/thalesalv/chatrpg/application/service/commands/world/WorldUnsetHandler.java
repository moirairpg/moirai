package es.thalesalv.chatrpg.application.service.commands.world;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Component
@Transactional
@RequiredArgsConstructor
public class WorldUnsetHandler {

    private final ChannelRepository channelRepository;
    private static final int DELETE_EPHEMERAL_TIMER = 20;
    private static final String EXCEPTION_PARSING_ARGUMENTS = "Exception caught processing arguments of unset command";
    private static final String USER_COMMAND_CHCONFIG_NOT_FOUND = "User tried to delete a config from a channel that has no config attached to it";
    private static final String CONFIG_ID_NOT_FOUND = "There is no channel configuration attached to this channel.";
    private static final String WORLD_UNLINKED_CHANNEL_CONFIG = "World `{0}` was unlinked from configuration ID `{1}`";
    private static final String ERROR_SETTING_CHANNEL_CONFIG = "Error unsetting channel config";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when unsetting the channel config. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldUnsetHandler.class);

    public void handleCommand(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Detaching world from channel config {} (channel ID {})", event.getChannel()
                .getName(),
                event.getChannel()
                        .getId());
        try {
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(config -> {
                        final String worldName = config.getChannelConfig()
                                .getWorld()
                                .getName();
                        config.getChannelConfig()
                                .setWorld(null);
                        event.reply(MessageFormat.format(WORLD_UNLINKED_CHANNEL_CONFIG, worldName, config.getId()))
                                .setEphemeral(true)
                                .queue(reply -> reply.deleteOriginal()
                                        .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
                        return config;
                    })
                    .orElseThrow(() -> new ChannelConfigurationNotFoundException(CONFIG_ID_NOT_FOUND));
        } catch (IllegalArgumentException e) {
            LOGGER.debug(EXCEPTION_PARSING_ARGUMENTS, e);
            event.reply(e.getMessage())
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (ChannelConfigurationNotFoundException e) {
            LOGGER.debug(USER_COMMAND_CHCONFIG_NOT_FOUND, e);
            event.reply(CONFIG_ID_NOT_FOUND)
                    .setEphemeral(true)
                    .queue(reply -> reply.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_CHANNEL_CONFIG, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN)
                    .setEphemeral(true)
                    .queue(reply -> reply.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_TIMER, TimeUnit.SECONDS));
        }
    }
}