package me.moirai.discordbot.common.dbutil;

import java.time.OffsetDateTime;

import me.moirai.discordbot.infrastructure.outbound.persistence.AssetEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AssetBaseDataAssigner {

    @PreUpdate
    @PrePersist
    public void setDate(AssetEntity asset) {

        OffsetDateTime now = OffsetDateTime.now();
        if (asset.getCreationDate() == null) {
            asset.setCreationDate(now);
        }

        asset.setLastUpdateDate(now);
    }
}
