package es.thalesalv.chatrpg.application.service.commands.chconfig;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.WorldRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class WorldChConfigCommandService implements DiscordCommand {

    private final WorldRepository worldRepository;
    private final ChannelRepository channelRepository;

    private static final String ERROR_SETTING_CHANNEL_CONFIG = "Error attaching world to channel config";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when attaching world to channel config. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldChConfigCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for attaching world to channel config");
        try {
            event.deferReply();
            final String worldId = event.getOption("world-id").getAsString();
            worldRepository.findById(worldId).map(world -> {
                return channelRepository.findByChannelId(event.getChannel().getId())
                        .filter(channel -> null != channel)
                        .map(channel -> attachWorldToConfig(channel, world, event))
                        .orElseThrow(() -> new ChannelConfigurationNotFoundException("Could not find a configuration for the current channel"));
            })
            .orElseThrow(() -> new WorldNotFoundException("Could not find the world with the request ID"));
        } catch (ChannelConfigurationNotFoundException e) {
            LOGGER.debug("User tried to find a channel config that does not exist", e);
            event.reply("The requested channel configuration does not exist.").setEphemeral(true).queue();
        } catch (WorldNotFoundException e) {
            LOGGER.debug("User tried to find a world that does not exist", e);
            event.reply("The requested world does not exist.").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_CHANNEL_CONFIG, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true).queue();
        }
    }

    private ChannelEntity attachWorldToConfig(final ChannelEntity channel, final WorldEntity world, final SlashCommandInteractionEvent event) {

        channel.setWorld(world);
        channelRepository.save(channel);
        event.reply(MessageFormat.format(
                "World `{0}` was linked to configuration to the configuration of channel `{1}`",
                world.getName(), channel.getId())).setEphemeral(true)
                .complete();

        return channel;
    }
}
