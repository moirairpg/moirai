package es.thalesalv.chatrpg.application.service.commands.chconfig;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
import es.thalesalv.chatrpg.adapters.data.db.entity.WorldEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.WorldRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.application.util.NanoId;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

@Service
@Transactional
@RequiredArgsConstructor
public class SetChConfigCommandService implements DiscordCommand {

    private final WorldRepository worldRepository;
    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;

    private static final String USER_COMMAND_WORLD_NOT_FOUND = "User tried to find a world that does not exist";
    private static final String USER_COMMAND_CHCONFIG_NOT_FOUND = "User tried to find a channel config that does not exist";
    private static final String WORLD_ID_NOT_FOUND = "The world with the requested ID does not exist.";
    private static final String CONFIG_ID_NOT_FOUND = "The channel configuration with the requested ID does not exist.";
    private static final String WORLD_LINKED_CHANNEL_CONFIG = "World `{0}` was linked to configuration to the configuration of channel `{1}` (with persona `{2}`)";
    private static final String CHANNEL_LINKED_CONFIG = "Channel `{0}` was linked to configuration `{1}` (with persona `{2}`).";
    private static final String ERROR_SETTING_CHANNEL_CONFIG = "Error defining channel config";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when defining the channel config. Please try again.";

    private static final Logger LOGGER = LoggerFactory.getLogger(SetChConfigCommandService.class);

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for setting channel config or world");
        try {
            event.deferReply();
            Optional.ofNullable(event.getOption("config-id"))
                    .map(OptionMapping::getAsString)
                    .map(configId -> setChannelConfig(configId, event))
                    .orElseGet(() -> Optional.ofNullable(event.getOption("world-id"))
                            .map(OptionMapping::getAsString)
                            .map(worldId -> setWorld(worldId, event))
                            .orElseThrow(() -> new WorldNotFoundException(WORLD_ID_NOT_FOUND)));
        } catch (ChannelConfigurationNotFoundException e) {
            LOGGER.debug(USER_COMMAND_CHCONFIG_NOT_FOUND, e);
            event.reply(CONFIG_ID_NOT_FOUND).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        } catch (WorldNotFoundException e) {
            LOGGER.debug(USER_COMMAND_WORLD_NOT_FOUND, e);
            event.reply(WORLD_ID_NOT_FOUND).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        } catch (Exception e) {
            LOGGER.error(ERROR_SETTING_CHANNEL_CONFIG, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true).queue(reply -> {
                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
            });
        }
    }

    private ChannelEntity setChannelConfig(final String configId, final SlashCommandInteractionEvent event) {

        LOGGER.debug("Attaching channel config ID {} to channel {} (channel ID {})", configId,
                event.getChannel().getName(), event.getChannel().getId());

        return channelConfigRepository.findById(configId)
                .map(config -> {
                    final ChannelEntity entity = channelRepository.findByChannelId(event.getChannel().getId())
                            .map(e -> {
                                e.setChannelConfig(config);
                                e.setChannelId(event.getChannel().getId());
                                return e;
                            })
                            .orElseGet(() -> ChannelEntity.builder()
                                    .channelConfig(config)
                                    .channelId(event.getChannel().getId())
                                    .id(NanoId.randomNanoId())
                                    .build());

                    channelRepository.save(entity);
                    event.reply(MessageFormat.format(CHANNEL_LINKED_CONFIG, event.getChannel().getName(),
                            entity.getId(), config.getPersona().getName())).setEphemeral(true).queue(reply -> {
                                reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                            });

                    return entity;
                })
                .orElseThrow(() -> new ChannelConfigurationNotFoundException(CONFIG_ID_NOT_FOUND));
    }

    private ChannelEntity setWorld(final String worldId, final SlashCommandInteractionEvent event) {

        LOGGER.debug("Attaching world ID {} to channel {} (channel ID {})", worldId,
                event.getChannel().getName(), event.getChannel().getId());

        return worldRepository.findById(worldId)
                .map(world -> channelRepository.findByChannelId(event.getChannel().getId())
                        .filter(channel -> null != channel)
                        .map(channel -> attachWorldToConfig(channel, world, event))
                        .orElseThrow(() -> new ChannelConfigurationNotFoundException(CONFIG_ID_NOT_FOUND)))
                .orElseThrow(() -> new WorldNotFoundException(WORLD_ID_NOT_FOUND));
    }

    private ChannelEntity attachWorldToConfig(final ChannelEntity channel, final WorldEntity world,
            final SlashCommandInteractionEvent event) {

        LOGGER.debug("World {} will be attached to channel {} (channel config ID {})", world.getName(),
                event.getChannel().getName(), channel.getId());

        channel.getChannelConfig().setWorld(world);
        channelRepository.save(channel);
        event.reply(MessageFormat.format(WORLD_LINKED_CHANNEL_CONFIG,
                world.getName(), channel.getId(), channel.getChannelConfig().getPersona().getName()))
                .setEphemeral(true).queue(reply -> {
                    reply.deleteOriginal().queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS);
                });

        return channel;
    }
}
