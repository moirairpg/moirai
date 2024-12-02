--liquibase formatted sql
--changeset moirai:1733076841_create_table_favorite
--preconditions onFail:HALT, onError:HALT

CREATE TABLE favorite (
    player_discord_id VARCHAR(100) NOT NULL,
    asset_id VARCHAR(100) NOT NULL,
    asset_type VARCHAR(20) NOT NULL,
    PRIMARY KEY(player_discord_id, asset_id, asset_type)
);

--rollback DROP TABLE favorite