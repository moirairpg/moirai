package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

import java.time.OffsetDateTime;

import es.thalesalv.chatrpg.common.dbutil.AssetBaseDataAssigner;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@EntityListeners(AssetBaseDataAssigner.class)
public abstract class AssetEntity {

    @Column(name = "creator_discord_id")
    protected String creatorDiscordId;

    @Column(name = "creation_date", nullable = false)
    protected OffsetDateTime creationDate;

    @Column(name = "last_update_date", nullable = false)
    protected OffsetDateTime lastUpdateDate;

    protected AssetEntity(String creatorDiscordId, OffsetDateTime creationDate, OffsetDateTime lastUpdateDate) {
        this.creatorDiscordId = creatorDiscordId;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
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
}
