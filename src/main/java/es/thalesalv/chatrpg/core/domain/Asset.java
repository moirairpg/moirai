package es.thalesalv.chatrpg.core.domain;

import java.time.OffsetDateTime;

public abstract class Asset {

    private final String creatorDiscordId;
    private final OffsetDateTime creationDate;
    private final OffsetDateTime lastUpdateDate;

    protected Asset(String creatorDiscordId, OffsetDateTime creationDate, OffsetDateTime lastUpdateDate) {
        this.creatorDiscordId = creatorDiscordId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
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
}
