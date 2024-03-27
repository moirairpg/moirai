package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import reactor.core.publisher.Mono;

public interface DiscordUserDetailsPort {

    Mono<DiscordUserDataResponse> retrieveLoggedUser(String token);
    Mono<DiscordUserDataResponse> retrieveUserById(String token, String discordUserId);
}
