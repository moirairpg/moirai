package es.thalesalv.chatrpg.infrastructure.outbound.adapter;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import es.thalesalv.chatrpg.core.application.port.DiscordUserDetailsPort;
import es.thalesalv.chatrpg.core.application.query.discord.DiscordUserDetails;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor(onConstructor_ = { @Lazy })
public class DiscordUserDetailsAdapter implements DiscordUserDetailsPort {

    private final GatewayDiscordClient discordClient;

    @Override
    public Mono<DiscordUserDetails> getUserById(String userDiscordId) {

        return discordClient.getUserById(Snowflake.of(userDiscordId))
                .map(user -> DiscordUserDetails.builder()
                        .id(user.getId().asString())
                        .globalName(user.getGlobalName().orElse(null))
                        .username(user.getUsername())
                        .mention(user.getMention())
                        .build());
    }
}
