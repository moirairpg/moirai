package me.moirai.discordbot.infrastructure.inbound.api.mapper;

import org.springframework.stereotype.Component;

import me.moirai.discordbot.core.application.usecase.discord.userdetails.DiscordUserDetailsResult;
import me.moirai.discordbot.infrastructure.inbound.api.response.UserDataResponse;

@Component
public class UserDataResponseMapper {

    public UserDataResponse toResponse(DiscordUserDetailsResult result) {

        return UserDataResponse.builder()
                .id(result.getId())
                .avatar(result.getAvatar())
                .globalNickname(result.getGlobalNickname())
                .username(result.getUsername())
                .build();
    }
}
