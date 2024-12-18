package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureRememberByChannelId extends UseCase<Void> {

    private final String remember;
    private final String channelId;

    private UpdateAdventureRememberByChannelId(String remember, String channelId) {
        this.remember = remember;
        this.channelId = channelId;
    }

    public static UpdateAdventureRememberByChannelId build(String remember, String channelId) {
        return new UpdateAdventureRememberByChannelId(remember, channelId);
    }

    public String getRemember() {
        return remember;
    }

    public String getChannelId() {
        return channelId;
    }
}