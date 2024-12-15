package me.moirai.discordbot.core.application.usecase.adventure.request;

import me.moirai.discordbot.common.usecases.UseCase;

public final class UpdateAdventureNudgeByChannelId extends UseCase<Void> {

    private final String nudge;
    private final String channelId;

    private UpdateAdventureNudgeByChannelId(String nudge, String channelId) {
        this.nudge = nudge;
        this.channelId = channelId;
    }

    public static UpdateAdventureNudgeByChannelId build(String nudge, String channelId) {
        return new UpdateAdventureNudgeByChannelId(nudge, channelId);
    }

    public String getNudge() {
        return nudge;
    }

    public String getChannelId() {
        return channelId;
    }
}