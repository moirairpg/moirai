package es.thalesalv.chatrpg.application.service.commands.world;

import java.text.MessageFormat;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

import es.thalesalv.chatrpg.adapters.data.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.mapper.chconfig.ChannelEntityToDTO;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigNotFoundException;
import es.thalesalv.chatrpg.domain.exception.WorldNotFoundException;
import es.thalesalv.chatrpg.domain.model.chconf.ChannelConfig;
import es.thalesalv.chatrpg.domain.model.chconf.World;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class GetWorldCommandService implements DiscordCommand {

    private final ObjectWriter prettyPrintObjectMapper;
    private final ChannelEntityToDTO channelEntityToDTO;

    private final ChannelRepository channelRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(GetWorldCommandService.class);

    private static final int DELETE_EPHEMERAL_20_SECONDS = 20;
    private static final String PUBLIC = "public";
    private static final String NO_CONFIG_OR_WORLD_ATTACHED = "This channel does not have a configuration with a valid world/lorebook attached to it.";
    private static final String ERROR_RETRIEVE = "An error occurred while retrieving world data";
    private static final String USER_ERROR_RETRIEVE = "There was an error parsing your request. Please try again.";
    private static final String ERROR_SERIALIZATION = "Error serializing entry data.";
    private static final String WORLD_RETRIEVED = "Retrieved world with name **{0}**.\n```json\n{1}```";

    @Override
    public void handle(final SlashCommandInteractionEvent event) {

        try {
            LOGGER.debug("Received slash command for lore entry retrieval");
            event.deferReply();
            channelRepository.findByChannelId(event.getChannel()
                    .getId())
                    .map(channelEntityToDTO)
                    .map(channel -> {
                        return Optional.ofNullable(channel.getChannelConfig())
                                .map(ChannelConfig::getWorld)
                                .filter(filterWorlds(event))
                                .map(w -> cleanWorld(w, event))
                                .map(world -> {
                                    try {
                                        final String worldJson = prettyPrintObjectMapper.writeValueAsString(world);
                                        event.reply(MessageFormat.format(WORLD_RETRIEVED, world.getName(), worldJson))
                                                .setEphemeral(true)
                                                .complete();
                                    } catch (JsonProcessingException e) {
                                        LOGGER.error(ERROR_SERIALIZATION, e);
                                        event.reply(ERROR_RETRIEVE)
                                                .setEphemeral(true)
                                                .queue(m -> m.deleteOriginal()
                                                        .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
                                    }

                                    return world;
                                })
                                .orElseThrow(() -> new WorldNotFoundException());
                    })
                    .orElseThrow(ChannelConfigNotFoundException::new);
        } catch (ChannelConfigNotFoundException | WorldNotFoundException e) {
            LOGGER.info(NO_CONFIG_OR_WORLD_ATTACHED);
            event.reply(NO_CONFIG_OR_WORLD_ATTACHED)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        } catch (Exception e) {
            LOGGER.error(ERROR_RETRIEVE, e);
            event.reply(USER_ERROR_RETRIEVE)
                    .setEphemeral(true)
                    .queue(m -> m.deleteOriginal()
                            .queueAfter(DELETE_EPHEMERAL_20_SECONDS, TimeUnit.SECONDS));
        }
    }

    private Predicate<World> filterWorlds(final SlashCommandInteractionEvent event) {

        return w -> w.getVisibility().equals(PUBLIC)
                || w.getOwner().equals(event.getUser().getId());
    }

    private World cleanWorld(final World world, final SlashCommandInteractionEvent event) {

        final String ownerName = event.getJDA()
                .getUserById(world.getOwner())
                .getName();
        world.setOwner(ownerName);
        world.setLorebook(null);
        world.setInitialPrompt(null);
        return world;
    }
}
