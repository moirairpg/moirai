--liquibase formatted sql
--changeset moirai:1733076841_create_table_favorite
--preconditions onFail:HALT, onError:HALT

CREATE TABLE favorite (
    id VARCHAR(100) PRIMARY KEY,
    player_discord_id VARCHAR(100) NOT NULL,
    asset_id VARCHAR(100) NOT NULL,
    asset_type VARCHAR(20) NOT NULL
);

--rollback DROP TABLE favorite CASCADE;