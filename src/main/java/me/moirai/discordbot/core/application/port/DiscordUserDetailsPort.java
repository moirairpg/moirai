package me.moirai.discordbot.core.application.port;

import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;
import reactor.core.publisher.Mono;

public interface DiscordUserDetailsPort {

    Mono<DiscordUserDetails> getUserById(String userDiscordId);
}
