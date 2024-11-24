package me.moirai.discordbot.infrastructure.outbound.persistence;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import me.moirai.discordbot.common.dbutil.AssetBaseDataAssigner;

@MappedSuperclass
@EntityListeners(AssetBaseDataAssigner.class)
public abstract class AssetEntity {

    @Column(name = "creator_discord_id")
    protected String creatorDiscordId;

    @Column(name = "creation_date", nullable = false)
    protected OffsetDateTime creationDate;

    @Column(name = "last_update_date", nullable = false)
    protected OffsetDateTime lastUpdateDate;

    @Version
    private int version;

    protected AssetEntity(String creatorDiscordId, OffsetDateTime creationDate, OffsetDateTime lastUpdateDate, int version) {
        this.creatorDiscordId = creatorDiscordId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.version = version;
    }

    protected AssetEntity() {
        super();
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

    public void setCreatorDiscordId(String creatorDiscordId) {
        this.creatorDiscordId = creatorDiscordId;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastUpdateDate(OffsetDateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
