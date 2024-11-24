package me.moirai.discordbot.core.domain;

import java.time.OffsetDateTime;

public abstract class Asset {

    private final String creatorDiscordId;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;
    private final int version;

    protected Asset(String creatorDiscordId,
            OffsetDateTime creationDate,
            OffsetDateTime lastUpdateDate,
            int version) {

        this.creatorDiscordId = creatorDiscordId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.version = version;
    }

    public String getCreatorDiscordId() {
        return creatorDiscordId;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public OffsetDateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public int getVersion() {
        return version;
    }
}
