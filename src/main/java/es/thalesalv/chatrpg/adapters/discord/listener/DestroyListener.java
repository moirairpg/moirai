package es.thalesalv.chatrpg.adapters.discord.listener;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

@RequiredArgsConstructor
public class DestroyListener {

    @Value("${chatrpg.discord.status-channel-id}")
    private String statusChannelId;
    private final JDA jda;
    private static final Logger LOGGER = LoggerFactory.getLogger(DestroyListener.class);

    @PreDestroy
    public void beforeDestroy() {

        try {
            Optional.ofNullable(statusChannelId)
                    .filter(StringUtils::isNotEmpty)
                    .ifPresent(id -> jda.getChannelById(TextChannel.class, id)
                            .sendMessage(jda.getSelfUser()
                                    .getName() + " is ready to chat!")
                            .complete());
        } catch (Exception e) {
            LOGGER.error("Error during destroy", e);
        }
    }
}
