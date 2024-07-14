package me.moirai.discordbot.core.application.usecase.channelconfig.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.channelconfig.result.GetChannelConfigResult;

public final class GetChannelConfigById extends UseCase<GetChannelConfigResult> {

    private final String id;
    private final String requesterDiscordId;

    private GetChannelConfigById(String id, String requesterDiscordId) {
        this.id = id;
        this.requesterDiscordId = requesterDiscordId;
    }

    public static GetChannelConfigById build(String id, String requesterDiscordId) {

        return new GetChannelConfigById(id, requesterDiscordId);
    }

    public String getId() {
        return id;
    }

    public String getRequesterDiscordId() {
        return requesterDiscordId;
    }
}
