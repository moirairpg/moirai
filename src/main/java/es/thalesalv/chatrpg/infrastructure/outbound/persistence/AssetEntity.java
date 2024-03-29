package es.thalesalv.chatrpg.infrastructure.outbound.persistence;

import java.time.OffsetDateTime;

import es.thalesalv.chatrpg.common.dbutil.AssetBaseDataAssigner;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
