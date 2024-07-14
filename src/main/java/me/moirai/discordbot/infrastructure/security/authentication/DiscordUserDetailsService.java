package me.moirai.discordbot.infrastructure.security.authentication;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import me.moirai.discordbot.core.application.port.DiscordAuthenticationPort;
import reactor.core.publisher.Mono;

@Service
public class DiscordUserDetailsService implements ReactiveUserDetailsService {

    private static final String BEARER = "Bearer ";

    private final DiscordAuthenticationPort discordAuthenticationPort;

    public DiscordUserDetailsService(DiscordAuthenticationPort discordAuthenticationPort) {
        this.discordAuthenticationPort = discordAuthenticationPort;
    }

    @Override
    public Mono<UserDetails> findByUsername(String token) {

        return discordAuthenticationPort.retrieveLoggedUser(token)
                .map(userDetails -> DiscordPrincipal.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .authorizationToken(token.replace(BEARER, EMPTY))
                        .build());
    }
}
