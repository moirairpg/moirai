package es.thalesalv.chatrpg.application.service.commands.chconfig;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.data.db.entity.ChannelEntity;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelConfigRepository;
import es.thalesalv.chatrpg.adapters.data.db.repository.ChannelRepository;
import es.thalesalv.chatrpg.application.service.commands.DiscordCommand;
import es.thalesalv.chatrpg.domain.exception.ChannelConfigurationNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Service
@Transactional
@RequiredArgsConstructor
public class SetChConfigCommandService implements DiscordCommand {

    private final ChannelRepository channelRepository;
    private final ChannelConfigRepository channelConfigRepository;

    private static final String ERROR_EDITING = "Error editing message";
    private static final String SOMETHING_WRONG_TRY_AGAIN = "Something went wrong when editing the message. Please try again.";
    private static final Logger LOGGER = LoggerFactory.getLogger(SetChConfigCommandService.class);

    @Override
    public void handle(SlashCommandInteractionEvent event) {

        LOGGER.debug("Received slash command for message edition");
        try {
            event.deferReply();
            final String configId = event.getOption("config-id").getAsString();
            channelConfigRepository.findById(configId)
                    .map(config -> {
                        channelRepository.findByChannelId(event.getChannel().getId())
                                .ifPresent(channelRepository::delete);

                        final ChannelEntity entity = ChannelEntity.builder()
                                .channelConfig(config)
                                .channelId(event.getChannel().getId())
                                .build();

                        channelRepository.save(entity);
                        event.reply(MessageFormat.format("Channel {0} was linked to configuration #{1}.",
                                event.getChannel().getName(), entity.getId())).setEphemeral(true).complete();

                        return entity;
                    })
                    .orElseThrow(() -> new ChannelConfigurationNotFoundException("Could not find configuration with provided ID"));
        } catch (ChannelConfigurationNotFoundException e) {
            LOGGER.debug("User tried to find a channel config that does not exist", e);
            event.reply("The request channel configuration does not exist.").setEphemeral(true).queue();
        } catch (Exception e) {
            LOGGER.error(ERROR_EDITING, e);
            event.reply(SOMETHING_WRONG_TRY_AGAIN).setEphemeral(true).queue();
        }
    }
}
