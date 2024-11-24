package me.moirai.discordbot.core.application.usecase.discord.userdetails;

import me.moirai.discordbot.common.annotation.UseCaseHandler;
import me.moirai.discordbot.common.exception.AssetNotFoundException;
import me.moirai.discordbot.common.usecases.AbstractUseCaseHandler;
import me.moirai.discordbot.core.application.port.DiscordUserDetailsPort;
import me.moirai.discordbot.core.application.usecase.discord.DiscordUserDetails;

@UseCaseHandler
public class GetUserDetailsByIdHandler extends AbstractUseCaseHandler<GetUserDetailsById, DiscordUserDetailsResult> {

    private final DiscordUserDetailsPort discordUserDetailsPort;

    public GetUserDetailsByIdHandler(DiscordUserDetailsPort discordUserDetailsPort) {
        this.discordUserDetailsPort = discordUserDetailsPort;
    }

    @Override
    public DiscordUserDetailsResult execute(GetUserDetailsById useCase) {

        DiscordUserDetails user = discordUserDetailsPort.getUserById(useCase.getDiscordUserId())
                .orElseThrow(() -> new AssetNotFoundException("The user requested was not found by Discord"));

        return DiscordUserDetailsResult.builder()
                .id(user.getId())
                .globalNickname(user.getNickname())
                .username(user.getUsername())
                .avatar(user.getAvatarUrl())
                .build();
    }
}
