package es.thalesalv.chatrpg.application.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.adapters.rest.client.DiscordApiService;
import es.thalesalv.chatrpg.application.mapper.JDAUserToDiscordUser;
import es.thalesalv.chatrpg.domain.model.discord.DiscordAuthRequest;
import es.thalesalv.chatrpg.domain.model.discord.DiscordUserData;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DiscordAuthService {

    @Value("${chatrpg.discord.api-token}")
    private String discordApiToken;

    @Value("${chatrpg.discord.oauth.client-id}")
    private String clientId;

    @Value("${chatrpg.discord.oauth.client-secret}")
    private String clientSecret;

    @Value("${chatrpg.discord.oauth.redirect-url}")
    private String redirectUrl;

    private final JDA jda;
    private final JDAUserToDiscordUser jdaUserToDiscordUser;
    private final DiscordApiService discordApiService;
    private final UserDefinitionsService userDefinitionsService;

    private static final String DISCORD_SCOPE = "identify";
    private static final String DISCORD_GRANT_TYPE = "authorization_code";

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordAuthService.class);

    public DiscordUserData retrieveDiscordUserById(final String userId) {

        LOGGER.debug("Retrieving discord user by ID {}", userId);
        final User retrievedUser = jda.retrieveUserById(userId)
                .complete();
        return jdaUserToDiscordUser.apply(retrievedUser);
    }

    public Mono<DiscordUserData> authenticate(final String authCode) {

        LOGGER.info("Authenticating user on Discord");
        final DiscordAuthRequest request = DiscordAuthRequest.builder()
                .code(authCode)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUrl)
                .scope(DISCORD_SCOPE)
                .grantType(DISCORD_GRANT_TYPE)
                .build();

        return discordApiService.authenticate(request)
                .flatMap(response -> {
                    return discordApiService.retrieveLoggedUser(response.getAccessToken())
                            .map(user -> {
                                userDefinitionsService.persistUser(user);
                                return user;
                            });
                });

    }
}
