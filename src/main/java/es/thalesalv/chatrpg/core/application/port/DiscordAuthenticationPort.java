package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordUserDataResponse;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.outbound.adapter.request.DiscordTokenRevocationRequest;
import reactor.core.publisher.Mono;

public interface DiscordAuthenticationPort {

    Mono<DiscordAuthResponse> authenticate(DiscordAuthRequest request);

    Mono<DiscordUserDataResponse> retrieveLoggedUser(String token);

    Mono<Void> logout(DiscordTokenRevocationRequest request);
}
