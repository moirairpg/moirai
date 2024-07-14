package me.moirai.discordbot.infrastructure.outbound.adapter;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import reactor.core.publisher.Mono;

@Component
public class DiscordUserDetailsAdapter implements DiscordUserDetailsPort {

    private final GatewayDiscordClient discordClient;

    @Lazy
    public DiscordUserDetailsAdapter(GatewayDiscordClient discordClient) {
        this.discordClient = discordClient;
    }

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
