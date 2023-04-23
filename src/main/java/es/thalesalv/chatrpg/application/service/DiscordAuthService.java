package es.thalesalv.chatrpg.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.application.mapper.JDAUserToDiscordUser;
import es.thalesalv.chatrpg.domain.model.DiscordUserData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

@Service
@RequiredArgsConstructor
public class DiscordAuthService {

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    private final JDA jda;
    private final JDAUserToDiscordUser jdaUserToDiscordUser;

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordAuthService.class);

    public DiscordUserData retrieveDiscordUserById(final String userId) {

        LOGGER.debug("Retrieving discord user by ID {}", userId);
        final User retrievedUser = jda.retrieveUserById(userId).complete();
        return jdaUserToDiscordUser.apply(retrievedUser);
    }
}
