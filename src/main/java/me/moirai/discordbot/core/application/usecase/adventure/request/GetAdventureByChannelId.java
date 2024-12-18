package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;
import me.moirai.discordbot.core.application.usecase.adventure.result.GetAdventureResult;

public final class GetAdventureByChannelId extends UseCase<GetAdventureResult> {

    private final String channelId;

    private GetAdventureByChannelId(String channelId) {
        this.channelId = channelId;
    }

    public static GetAdventureByChannelId build(String channelId) {
        return new GetAdventureByChannelId(channelId);
    }

    public String getChannelId() {
        return channelId;
    }
}
