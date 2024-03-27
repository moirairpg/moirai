package es.thalesalv.chatrpg.infrastructure.security.authentication;

import static org.apache.commons.lang3.StringUtils.EMPTY;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import es.thalesalv.chatrpg.core.application.port.DiscordUserDetailsPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DiscordUserDetailsService implements ReactiveUserDetailsService {

    private static final String BEARER = "Bearer ";

    private final DiscordUserDetailsPort discordUserDetailsPort;

    @Override
    public Mono<UserDetails> findByUsername(String token) {

        return discordUserDetailsPort.retrieveLoggedUser(token)
                .map(userDetails -> DiscordPrincipal.builder()
                        .id(userDetails.getId())
                        .username(userDetails.getUsername())
                        .email(userDetails.getEmail())
                        .authorizationToken(token.replace(BEARER, EMPTY))
                        .build());
    }
}
