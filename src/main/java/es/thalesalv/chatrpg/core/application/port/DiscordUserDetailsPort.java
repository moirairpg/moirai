package es.thalesalv.chatrpg.core.application.port;

import es.thalesalv.chatrpg.core.application.usecase.discord.DiscordUserDetails;
import reactor.core.publisher.Mono;

public interface DiscordUserDetailsPort {

    Mono<DiscordUserDetails> getUserById(String userDiscordId);
}
