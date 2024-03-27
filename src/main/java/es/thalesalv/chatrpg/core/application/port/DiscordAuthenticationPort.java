package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordAuthRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.request.DiscordTokenRevocationRequest;
import es.thalesalv.chatrpg.infrastructure.inbound.api.response.DiscordAuthResponse;
import reactor.core.publisher.Mono;

public interface DiscordAuthenticationPort {

    Mono<DiscordAuthResponse> authenticate(DiscordAuthRequest request);

    Mono<Void> logout(DiscordTokenRevocationRequest request);
}
