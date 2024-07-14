package me.moirai.discordbot.core.application.usecase.channelconfig.result;

import java.time.OffsetDateTime;

public final class UpdateChannelConfigResult {

    private final OffsetDateTime lastUpdatedDateTime;

    private UpdateChannelConfigResult(OffsetDateTime lastUpdatedDateTime) {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

    public static UpdateChannelConfigResult build(OffsetDateTime lastUpdatedDateTime) {

        return new UpdateChannelConfigResult(lastUpdatedDateTime);
    }

    public OffsetDateTime getLastUpdatedDateTime() {
        return lastUpdatedDateTime;
    }
}
