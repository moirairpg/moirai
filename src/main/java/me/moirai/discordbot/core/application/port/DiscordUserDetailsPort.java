package me.moirai.discordbot.core.application.port;

import java.util.Optional;

import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;

public interface DiscordUserDetailsPort {

    Optional<DiscordUserDetails> getUserById(String userDiscordId);

    Optional<DiscordUserDetails> getGuildMemberById(String userId, String guildId);

    DiscordUserDetails getBotUser();
}
